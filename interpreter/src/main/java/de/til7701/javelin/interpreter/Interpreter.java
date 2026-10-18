package de.til7701.javelin.interpreter;

import module de.til7701.javelin.ast;
import module de.til7701.javelin.common;
import de.til7701.javelin.interpreter.variable.PrimitiveVariable;
import de.til7701.javelin.interpreter.variable.StructVariable;
import de.til7701.javelin.interpreter.variable.Variable;
import de.til7701.javelin.interpreter.variable.VariableFactory;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
public final class Interpreter {

    private final Environment environment;
    private final VariableFactory variableFactory = new VariableFactory();
    private final Deque<Interrupt> interruptQueue = new ArrayDeque<>();
    private final StaticStore staticStore = new StaticStore();
    private boolean executingInterrupt = false;

    private Imports currentImports;

    public Interpreter(Environment environment) {
        this.environment = environment;
        resetImports();
    }

    private void resetImports() {
        currentImports = environment.getPrimitiveImports();
    }

    public void interpret(Ast ast) {
        Stack context = new Stack();
        context.push(new StackFrame());
        switch (ast) {
            case Script script -> {
                List<Statement> statements = script.statements();
                statements.forEach(statement -> executeStatement(statement, context));
            }
            case TypeDefinition _ -> throw new NotImplementedException();
        }
        log.debug("Context: {}", context);
        log.debug("Literals: {}", variableFactory.literalsToString());
    }

    private void executeStatement(Statement statement, Stack stack) {
        handleInterrupts();
        switch (statement) {
            case Import imp -> currentImports.addImport(imp);
            case VariableInitialization(_, String name, Expression value) ->
                    stack.initializeVariable(name, evaluateExpression(value, stack));
            case Assignment(_, Expression target, Expression value) -> {
                Variable t = evaluateExpression(target, stack);
                t.set(evaluateExpression(value, stack));
            }
            case ReturnStatement(_, Expression value) -> {
                if (value != null) {
                    Variable result = evaluateExpression(value, stack);
                    StackFrame frame = stack.peek();
                    if (frame != null) frame.setReturnValue(result);
                }
            }
            case StatementList(_, List<Statement> statements) -> statements.forEach(s -> executeStatement(s, stack));
            case WhenStatement(_, boolean evalInstantly, Expression condition, Statement body) -> {
                Set<Variable> topVariables = topVariables(condition, stack);
                Stack stackSnapshot = stack.snapshot();
                Imports importsSnapshot = currentImports.snapshot();
                setupWhen(evalInstantly, stackSnapshot, importsSnapshot, condition, body, topVariables);
            }
            case Expression e -> evaluateExpression(e, stack);
        }
    }

    Variable evaluateExpression(Expression expression, Stack stack) {
        return switch (expression) {
            case NewExpression(_, Expression e) -> evaluateExpression(e, stack).createNew();
            case BooleanLiteralExpression(_, boolean value) -> variableFactory.fromBoolLiteral(value);
            case SignedIntegerLiteralExpression(_, long value, long bitCount) ->
                    variableFactory.fromILiteral(value, (int) bitCount);
            case UnsignedIntegerLiteralExpression(_, long value, long bitCount) ->
                    variableFactory.fromULiteral(value, (int) bitCount);
            case StringLiteralExpression(_, String value) -> variableFactory.fromStrLiteral(value);
            case SymbolExpression(_, String identifier) -> Objects.requireNonNull(stack.getVariable(identifier));
            case InstanceMethodCall(_, Expression instance, String name, List<Expression> arguments) -> {
                Variable instanceVariable = evaluateExpression(instance, stack);
                TypeDescriptor typeDescriptor = instanceVariable.descriptor();
                List<Variable> argumentValues = arguments.stream()
                        .map((Expression e) -> evaluateExpression(e, stack))
                        .toList();
                TypeDescriptor[] argumentTypes = argumentValues.stream()
                        .map(Variable::descriptor)
                        .toArray(TypeDescriptor[]::new);
                Metod method = typeDescriptor.findMethod(name, argumentTypes, environment)
                        .orElseThrow(() -> new RuntimeException("Method: " + name + " with args: " + Arrays.deepToString(argumentTypes) + " not found for class: " + typeDescriptor));
                yield switch (method) {
                    case JavaMetod javaMetod -> executeJavaMethod(javaMetod, argumentValues, instanceVariable);
                    case JavelinMetod javelinMetod ->
                            executeJavelinMethod(javelinMetod, argumentValues, instanceVariable, stack);
                    case TraitMetod _ -> throw new UnsupportedOperationException();
                };
            }
            case StaticMethodCall(_, Type type, String methodName, List<Expression> arguments) -> {
                TypeDescriptor typeDescriptor = environment.getKlassRegister().find(type, currentImports)
                        .orElseThrow(() -> new RuntimeException("Class not found for type: " + type));
                List<Variable> argumentValues = arguments.stream()
                        .map((Expression e) -> evaluateExpression(e, stack))
                        .toList();
                TypeDescriptor[] argumentTypes = argumentValues.stream()
                        .map(Variable::descriptor)
                        .toArray(TypeDescriptor[]::new);
                Metod method = typeDescriptor.findMethod(methodName, argumentTypes, environment)
                        .orElseThrow(() -> new RuntimeException("Method: " + methodName + " with args: " + Arrays.deepToString(argumentTypes) + " not found for class: " + typeDescriptor));
                yield switch (method) {
                    case JavaMetod javaMetod -> executeJavaMethod(javaMetod, argumentValues, null);
                    case JavelinMetod javelinMetod -> executeJavelinMethod(javelinMetod, argumentValues, null, stack);
                    case TraitMetod _ -> throw new UnsupportedOperationException();
                };
            }
            case BinaryExpression(_, Expression left, BinaryOperator binaryOperator, Expression right) -> {
                Variable leftV = evaluateExpression(left, stack);
                Variable rightV = evaluateExpression(right, stack);
                String methodName = binaryOperator.name().toLowerCase(Locale.ROOT);
                TypeDescriptor type = leftV.descriptor();
                TypeDescriptor[] argumentTypes = {leftV.descriptor(), rightV.descriptor()};
                Metod method = type.findMethod(methodName, argumentTypes, environment)
                        .orElseThrow(() -> new RuntimeException("Method: " + methodName + " with args: " + Arrays.deepToString(argumentTypes) + " not found for class: " + type));
                yield switch (method) {
                    case JavaMetod javaMetod -> executeJavaMethod(javaMetod, List.of(leftV, rightV), null);
                    case JavelinMetod javelinMetod ->
                            executeJavelinMethod(javelinMetod, List.of(leftV, rightV), null, stack);
                    case TraitMetod _ -> throw new UnsupportedOperationException();
                };
            }
            case ArrayLiteralCreation(_, List<Expression> values) -> {
                Variable[] variables = values.stream()
                        .map((Expression e) -> evaluateExpression(e, stack))
                        .toArray(Variable[]::new);
                TypeDescriptor elementType = variables[0].descriptor();
                yield variableFactory.asArray(variables, elementType);
            }
            case ArrayCreation(_, Expression defaultValue, Expression length) -> {
                Variable lengthVar = evaluateExpression(length, stack);
                int l = (int) ((U) lengthVar.javaValue()).getValue().getValue();
                Variable d = evaluateExpression(defaultValue, stack);
                Variable[] array = new Variable[l];
                Arrays.fill(array, d);
                yield variableFactory.asArray(array, d.descriptor());
            }
            case ConstructorCall(_, Type type, List<Expression> arguments) -> {
                TypeDescriptor typeDescriptor = environment.getKlassRegister().find(type, currentImports)
                        .orElseThrow(() -> new RuntimeException("Class not found for type: " + type));
                List<Variable> argumentValues = arguments.stream()
                        .map((Expression e) -> evaluateExpression(e, stack))
                        .toList();
                TypeDescriptor[] argumentTypes = argumentValues.stream()
                        .map(Variable::descriptor)
                        .toArray(TypeDescriptor[]::new);
                yield switch (typeDescriptor) {
                    case JavaKlass _ -> throw new NotImplementedException();
                    case JavelinKlass javelinKlass -> {
                        JavelinMetod method = javelinKlass.getConstructor(argumentTypes, environment)
                                .orElseThrow(() -> new RuntimeException("Constructor: with args: " + Arrays.deepToString(argumentTypes) + " not found for class: " + typeDescriptor));
                        Variable struct = initStruct(javelinKlass);
                        Stack s = new Stack();
                        s.push(new StackFrame());
                        s.initializeVariable("this", struct);
                        executeJavelinMethod(method, argumentValues, null, s);
                        yield struct;
                    }
                    default -> throw new UnsupportedOperationException();
                };
            }
            case InstanceFieldAccess(_, Expression instance, String fieldName) -> {
                Variable inst = evaluateExpression(instance, stack);
                if (inst instanceof StructVariable struct) {
                    yield struct.getField(fieldName);
                } else {
                    throw new RuntimeException("Instance is not a struct");
                }
            }
            case StaticFieldAccess(_, Type type, String fieldName) -> {
                TypeDescriptor typeDescriptor = environment.getKlassRegister().find(type, currentImports)
                        .orElseThrow(() -> new RuntimeException("Class not found for type: " + type));
                if (!staticStore.has(typeDescriptor)) staticStore.init(typeDescriptor, populateStatics(typeDescriptor));
                Variable value = staticStore.get(typeDescriptor, fieldName);
                yield Objects.requireNonNull(value);
            }
            case TypeCastExpression(_, Type targetType, Expression valueExpression) -> {
                Variable value = evaluateExpression(valueExpression, stack);
                TypeDescriptor valueType = value.descriptor();
                targetType = targetType.mapNames(currentImports::map);

                String methodName = "as__" + targetType.name().replace('.', '_');
                Metod method = valueType.findMethod(methodName, new TypeDescriptor[]{valueType}, environment)
                        .orElseThrow(() -> new RuntimeException("Method: " + methodName + " with args: " + valueType + " not found for class: " + valueType));
                yield switch (method) {
                    case JavaMetod javaMetod -> executeJavaMethod(javaMetod, List.of(value), null);
                    case JavelinMetod javelinMetod -> executeJavelinMethod(javelinMetod, List.of(value), null, stack);
                    case TraitMetod _ -> throw new UnsupportedOperationException();
                };
            }
            default -> throw new NotImplementedException(expression.toString());
        };
    }

    private Variable initStruct(JavelinKlass javelinKlass) {
        Map<String, Variable> fields = new HashMap<>();
        for (Field field : javelinKlass.fields()) {
            if (field.isStatic()) continue;
            String name = field.name();
            Stack instanceInitStack = new Stack();
            instanceInitStack.push(new StackFrame());
            Variable fieldInit = evaluateExpression(field.init(), instanceInitStack);
            fields.put(name, fieldInit);
        }
        return new StructVariable(javelinKlass, fields);
    }

    private Map<String, Variable> populateStatics(TypeDescriptor typeDescriptor) {
        Map<String, Variable> variables = new HashMap<>();
        Stack staticInitStack = new Stack();
        staticInitStack.push(new StackFrame());
        if (typeDescriptor instanceof JavelinKlass klass) {
            klass.fields().stream()
                    .filter(Field::isStatic)
                    .forEach(f -> {
                        Variable value = evaluateExpression(f.init(), staticInitStack);
                        variables.put(f.name(), value);
                        staticInitStack.initializeVariable(f.name(), value);
                    });
        }
        return variables;
    }

    private Variable executeJavaMethod(JavaMetod javaMetod, List<Variable> argumentValues, @Nullable Variable instance) {
        Object result;
        try {
            Method method = javaMetod.javaMethod();
            Object javaInstance = null;
            if (!javaMetod.isStatic() && instance != null) {
                javaInstance = instance.javaValue();
            }
            log.debug(javaMetod.toString());
            if (javaMetod.needsNatives())
                result = method.invoke(javaInstance, Stream.concat(Stream.of(environment.getNatives()), argumentValues.stream().map(Variable::javaValue)).toArray());
            else
                result = method.invoke(javaInstance, argumentValues.stream().map(Variable::javaValue).toArray());
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return variableFactory.fromJavaValue(result);
    }

    private Variable executeJavelinMethod(JavelinMetod javelinMetod, List<Variable> argumentValues, @Nullable Variable instance, Stack stack) {
        stack.push(new StackFrame());
        if (instance != null) stack.initializeVariable("this", instance);
        for (int i = 0; i < javelinMetod.parameterNames().length; i++) {
            stack.initializeVariable(javelinMetod.parameterNames()[i], argumentValues.get(i));
        }
        executeStatement(javelinMetod.body(), stack);
        StackFrame frame = stack.pop();
        return frame.getReturnValue();
    }

    private Set<Variable> topVariables(Expression expression, Stack context) {
        return switch (expression) {
            case SymbolExpression(_, String name) -> Set.of(Objects.requireNonNull(context.getVariable(name)));
            case NewExpression _,
                 BooleanLiteralExpression _,
                 StringLiteralExpression _ -> Set.of(evaluateExpression(expression, context));
            case BinaryExpression(_, Expression left, _, Expression right) -> {
                Collection<Variable> l = topVariables(left, context);
                Collection<Variable> r = topVariables(right, context);
                Set<Variable> result = new HashSet<>();
                result.addAll(l);
                result.addAll(r);
                yield result;
            }
            default -> throw new NotImplementedException(expression.toString());
        };
    }

    void interrupt(Interrupt item) {
        this.interruptQueue.addLast(item);
    }

    private void setupWhen(boolean evalInstantly, Stack stack, Imports imports, Expression condition, Statement body, Collection<Variable> topVariables) {
        topVariables.forEach(v -> v.listen(() -> evalWhen(stack, imports, condition, body)));
        if (evalInstantly) evalWhen(stack, imports, condition, body);
    }

    private void evalWhen(Stack stack, Imports imports, Expression condition, Statement body) {
        Variable result = this.evaluateExpression(condition, stack);
        if (result instanceof PrimitiveVariable primitiveVariable
                && primitiveVariable.javaValue() instanceof Bool bool
                && bool.isValue()) {
            this.interrupt(new Interrupt(body, stack, imports));
        }
    }

    private void handleInterrupts() {
        if (!executingInterrupt) {
            while (!interruptQueue.isEmpty()) {
                Interrupt interrupt = this.interruptQueue.pollFirst();
                Imports oldImports = currentImports;
                currentImports = interrupt.imports();

                executingInterrupt = true;
                Stack stack = interrupt.stack();
                stack.push(new StackFrame());
                executeStatement(interrupt.unterbrechungsbehandlungsprozedur(), stack);
                stack.pop();
                executingInterrupt = false;

                currentImports = oldImports;
            }
        }
    }

}
