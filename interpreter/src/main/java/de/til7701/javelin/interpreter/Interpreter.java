package de.til7701.javelin.interpreter;

import module de.til7701.javelin.ast;
import module de.til7701.javelin.common;
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
    private final Set<Long> runningInterrupts = new HashSet<>();
    private final StaticStore staticStore = new StaticStore();

    private Imports currentImports;

    public Interpreter(Environment environment) {
        this.environment = environment;
        resetImports();
    }

    private void resetImports() {
        currentImports = environment.getPrimitiveImports();
    }

    public void interpret(Ast ast) {
        Stack stack = new Stack();
        stack.push(new StackFrame());
        switch (ast) {
            case Script script -> {
                List<Statement> statements = script.statements();
                for (Statement statement : statements) {
                    executeStatement(statement, stack);
                }
            }
            case TypeDefinition _ -> throw new NotImplementedException();
        }
        log.debug("Stack: {}", stack);
        log.debug("Literals: {}", variableFactory.literalsToString());
    }

    private void executeStatement(Statement statement, Stack stack) {
        handleInterrupts(stack);
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
            case StatementList(_, List<Statement> statements) -> {
                for (Statement s : statements) {
                    executeStatement(s, stack);
                }
            }
            case WhenStatement(long id, _, boolean evalInstantly, Expression condition, Statement body) -> {
                When when = new When(this, id, evalInstantly, stack.snapshot(), condition, body, currentImports.snapshot());
                stack.peek().getWhens().add(when);
            }
            case Expression e -> evaluateExpression(e, stack);
        }
        handleInterrupts(stack);
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
            case SymbolExpression(_, String identifier) ->
                    Objects.requireNonNull(stack.getVariable(identifier), expression + "\n" + stack);
            case InstanceMethodCall(Span span, Expression instance, String name, List<Expression> arguments) -> {
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
                    case JavaMetod javaMetod ->
                            executeJavaMethod(span, javaMetod, argumentValues, instanceVariable, stack);
                    case JavelinMetod javelinMetod ->
                            executeJavelinMethod(javelinMetod, argumentValues, instanceVariable, stack);
                    case TraitMetod _ -> throw new UnsupportedOperationException();
                };
            }
            case StaticMethodCall(Span span, Type type, String methodName, List<Expression> arguments) -> {
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
                    case JavaMetod javaMetod -> executeJavaMethod(span, javaMetod, argumentValues, null, stack);
                    case JavelinMetod javelinMetod -> executeJavelinMethod(javelinMetod, argumentValues, null, stack);
                    case TraitMetod _ -> throw new UnsupportedOperationException();
                };
            }
            case BinaryExpression(Span span, Expression left, BinaryOperator binaryOperator, Expression right) -> {
                Variable leftV = evaluateExpression(left, stack);
                Variable rightV = evaluateExpression(right, stack);
                String methodName = binaryOperator.name().toLowerCase(Locale.ROOT);
                TypeDescriptor type = leftV.descriptor();
                TypeDescriptor[] argumentTypes = {leftV.descriptor(), rightV.descriptor()};
                Metod method = type.findMethod(methodName, argumentTypes, environment)
                        .orElseThrow(() -> new RuntimeException(span + " Method: " + methodName + " with args: " + Arrays.deepToString(argumentTypes) + " not found for class: " + type));
                yield switch (method) {
                    case JavaMetod javaMetod -> executeJavaMethod(span, javaMetod, List.of(leftV, rightV), null, stack);
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
                for (int i = 0; i < array.length; i++) {
                    array[i] = d.createNew();
                }
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
                        s.popDestroy();
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
            case TypeCastExpression(Span span, Type targetType, Expression valueExpression) -> {
                Variable value = evaluateExpression(valueExpression, stack);
                TypeDescriptor valueType = value.descriptor();
                targetType = targetType.mapNames(currentImports::map);

                String methodName = "as__" + targetType.name().replace('.', '_');
                Metod method = (switch (valueType) {
                    case Trait _, JavelinKlass _ ->
                            valueType.findMethod(methodName, new TypeDescriptor[]{valueType}, environment);
                    case JavaKlass _ ->
                            valueType.findMethod(methodName, new TypeDescriptor[]{valueType, PType.DESCRIPTOR.get()}, environment);
                }).orElseThrow(() -> new RuntimeException("Method: " + methodName + " with args: " + valueType.type().name() + ", " + PType.TYPE.name() + " not found for class: " + valueType));
                yield switch (method) {
                    case JavaMetod javaMetod ->
                            executeJavaMethod(span, javaMetod, List.of(value, new PrimitiveVariable(PType.of(targetType))), null, stack);
                    case JavelinMetod javelinMetod -> executeJavelinMethod(javelinMetod, List.of(value), null, stack);
                    case TraitMetod _ -> throw new UnsupportedOperationException();
                };
            }
            case LeftUnaryExpression _, RightUnaryExpression _, FloatLiteralExpression _ ->
                    throw new NotImplementedException(expression.toString());
        };
    }

    private Variable initStruct(JavelinKlass javelinKlass) {
        Map<String, Variable> fields = new HashMap<>();
        Stack instanceInitStack = new Stack();
        instanceInitStack.push(new StackFrame());
        for (Field field : javelinKlass.fields()) {
            if (field.isStatic()) continue;
            String name = field.name();
            Variable fieldInit = evaluateExpression(field.init(), instanceInitStack);
            fields.put(name, fieldInit);
            instanceInitStack.initializeVariable(name, fieldInit);
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

    private Variable executeJavaMethod(Span span, JavaMetod javaMetod, List<Variable> argumentValues, @Nullable Variable instance, Stack stack) {
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
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            switch (e.getCause()) {
                case ArrayIndexOutOfBoundsException ae when !argumentValues.isEmpty() && argumentValues.getFirst() instanceof PrimitiveVariable primitiveVariable && primitiveVariable.javaValue() instanceof Array ->
                        throw new InterpreterException.CollectionAccessOutOfBounds(span, stack, variableFactory.getLiteralStore(), ae.getMessage());
                case CastNegativeToUnsignedException ce ->
                        throw new InterpreterException.CastNegativeToUnsigned(span, stack, variableFactory.getLiteralStore(), ce.getMessage());
                case StringIndexOutOfBoundsException se ->
                        throw new InterpreterException.CastNegativeToUnsigned(span, stack, variableFactory.getLiteralStore(), se.getMessage());
                case null, default -> throw new RuntimeException(span.toString(), e);
            }
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
        StackFrame frame = stack.popDestroy();
        return frame.getReturnValue();
    }

    private void handleInterrupts(Stack stack) {
        outer:
        while (true) {
            for (StackFrame frame : stack) {
                if (frame.getInterruptQueue().isEmpty())
                    continue;
                Interrupt interrupt = frame.getInterruptQueue().peek();
                if (!runningInterrupts.contains(interrupt.mutexID())) {
                    frame.getInterruptQueue().pop();
                    executeInterrupt(interrupt);
                    continue outer;
                }
            }
            break;
        }
    }

    private void executeInterrupt(Interrupt interrupt) {
        Imports oldImports = currentImports;
        currentImports = interrupt.imports();

        runningInterrupts.add(interrupt.mutexID());

        Stack interruptStack = interrupt.stack();
        interruptStack.push(new StackFrame());
        executeStatement(interrupt.unterbrechungsbehandlungsprozedur(), interruptStack);
        interruptStack.popDestroy();

        runningInterrupts.remove(interrupt.mutexID());

        currentImports = oldImports;
    }

}
