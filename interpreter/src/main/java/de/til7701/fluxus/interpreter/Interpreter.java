package de.til7701.fluxus.interpreter;

import module de.til7701.fluxus.ast;
import module de.til7701.fluxus.common;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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

    public void interpret(Ast ast, @Nullable String fullyQualifiedFluxusName) {
        Stack stack = new Stack();
        switch (ast) {
            case Script script -> {
                stack.push(new StackFrame(script.span()));
                List<Statement> statements = script.statements();
                for (Statement statement : statements) {
                    executeStatement(statement, stack);
                }
            }
            case TypeDefinition typeDefinition -> {
                switch (typeDefinition) {
                    case TypeDefinition.ClassDefinition classDefinition -> {
                        TypeDefinition.MethodDefinition main = classDefinition.methods().stream()
                                .filter(m -> m.name().equals("main"))
                                .filter(m -> m.modifiers().stream().anyMatch(mod -> mod.value() == TypeDefinition.MethodModifierValue.STATIC))
                                .filter(m -> m.parameters().parameters().isEmpty())
                                .findAny().orElseThrow(() -> {
                                    stack.push(new StackFrame(classDefinition.span()));
                                    return new InterpreterException.MethodNotFound(
                                            typeDefinition.span(),
                                            stack, variableFactory.getLiteralStore(), "Main method not found"
                                    );
                                });
                        stack.push(new StackFrame(main.span()));
                        classDefinition.imports().forEach(c -> currentImports.addImport(c));
                        Objects.requireNonNull(fullyQualifiedFluxusName);
                        currentImports.addImport(new Statement.Import(Span.undefined(), fullyQualifiedFluxusName, null));
                        currentImports.addImport(new Statement.Import(Span.undefined(), fullyQualifiedFluxusName, "This"));
                        executeStatement(main.body(), stack);
                    }
                }
            }
        }
    }

    private void executeStatement(Statement statement, Stack stack) {
        handleInterrupts(stack);
        switch (statement) {
            case Statement.Import imp -> currentImports.addImport(imp);
            case Statement.VariableInitialization(_, String name, Expression value) ->
                    stack.initializeVariable(name, evaluateExpression(value, stack));
            case Statement.Assignment(_, Expression target, Expression value) -> {
                Variable t = evaluateExpression(target, stack);
                t.set(evaluateExpression(value, stack));
            }
            case Statement.Return(_, Expression value) -> {
                if (value != null) {
                    Variable result = evaluateExpression(value, stack);
                    throw new ReturnException(result);
                }
            }
            case Statement.StatementList(_, List<Statement> statements) -> {
                for (Statement s : statements) {
                    executeStatement(s, stack);
                }
            }
            case Statement.When(long id, _, boolean evalInstantly, Expression condition, Statement body) -> {
                When when = new When(this, id, evalInstantly, stack.snapshot(), condition, body, currentImports.snapshot());
                stack.peek().getWhens().add(when);
            }
            case Expression e -> evaluateExpression(e, stack);
        }
        handleInterrupts(stack);
    }

    Variable evaluateExpression(Expression expression, Stack stack) {
        return switch (expression) {
            case Expression.New(_, Expression e) -> evaluateExpression(e, stack).createNew();
            case Expression.BooleanLiteral(_, boolean value) -> variableFactory.fromBoolLiteral(value);
            case Expression.SignedIntegerLiteral(_, long value, long bitCount) ->
                    variableFactory.fromILiteral(value, bitCount);
            case Expression.UnsignedIntegerLiteral(_, long value, long bitCount) ->
                    variableFactory.fromULiteral(value, bitCount);
            case Expression.FloatLiteral(_, double value, long bitCount) ->
                    variableFactory.fromFLiteral(value, bitCount);
            case Expression.CharLiteral(_, String c) -> variableFactory.fromCharLiteral(c);
            case Expression.StringLiteral(Span span, String value) -> createString(stack, span, value);
            case Expression.Symbol(_, String identifier) -> {
                Variable v = stack.getVariable(identifier);
                if (v == null)
                    throw new NullPointerException(expression + "\n" + stack);
                yield v;
            }
            case Expression.InstanceMethodCall(
                    Span span, Expression instance, String name, List<Expression> arguments
            ) -> {
                Variable instanceVariable = evaluateExpression(instance, stack);
                TypeDescriptor typeDescriptor = instanceVariable.descriptor();
                Variable[] argumentValues = new Variable[arguments.size()];
                for (int i = 0; i < argumentValues.length; i++) {
                    Expression e = arguments.get(i);
                    Variable variable = evaluateExpression(e, stack);
                    argumentValues[i] = variable;
                }
                TypeDescriptor[] argumentTypes = new TypeDescriptor[argumentValues.length];
                for (int i = 0; i < argumentTypes.length; i++) {
                    argumentTypes[i] = argumentValues[i].descriptor();
                }
                Metod method = typeDescriptor.findMethod(name, argumentTypes, environment)
                        .orElseThrow(() -> new RuntimeException("Method: " + name + " with args: " + Arrays.deepToString(argumentTypes) + " not found for class: " + typeDescriptor));
                yield switch (method) {
                    case JavaMetod javaMetod ->
                            executeJavaMethod(span, javaMetod, argumentValues, instanceVariable, stack);
                    case FluxusMetod fluxusMetod ->
                            executeFluxusMethod(fluxusMetod, argumentValues, instanceVariable, stack);
                    case TraitMetod _ -> throw new UnsupportedOperationException();
                };
            }
            case Expression.StaticMethodCall(Span span, Type type, String methodName, List<Expression> arguments) -> {
                TypeDescriptor typeDescriptor = environment.getKlassRegister().find(type, currentImports)
                        .orElseThrow(() -> new RuntimeException("Class not found for type: " + type));
                Variable[] argumentValues = new Variable[arguments.size()];
                for (int i = 0; i < argumentValues.length; i++) {
                    Expression e = arguments.get(i);
                    Variable variable = evaluateExpression(e, stack);
                    argumentValues[i] = variable;
                }
                TypeDescriptor[] argumentTypes = new TypeDescriptor[argumentValues.length];
                for (int i = 0; i < argumentTypes.length; i++) {
                    argumentTypes[i] = argumentValues[i].descriptor();
                }
                Metod method = typeDescriptor.findMethod(methodName, argumentTypes, environment)
                        .orElseThrow(() -> new RuntimeException("Method: " + methodName + " with args: " + Arrays.deepToString(argumentTypes) + " not found for class: " + typeDescriptor));
                yield switch (method) {
                    case JavaMetod javaMetod -> executeJavaMethod(span, javaMetod, argumentValues, null, stack);
                    case FluxusMetod fluxusMetod -> executeFluxusMethod(fluxusMetod, argumentValues, null, stack);
                    case TraitMetod _ -> throw new UnsupportedOperationException();
                };
            }
            case Expression.LeftUnary(Span span, Expression.LeftUnaryOperator operator, Expression value) -> {
                Variable valueV = evaluateExpression(value, stack);
                String methodName = operator.name().toLowerCase(Locale.ROOT);
                TypeDescriptor type = valueV.descriptor();
                TypeDescriptor[] argumentTypes = {valueV.descriptor()};
                Metod method = type.findMethod(methodName, argumentTypes, environment)
                        .orElseThrow(() -> new RuntimeException(span + " Method: " + methodName + " with args: " + Arrays.deepToString(argumentTypes) + " not found for class: " + type));
                yield switch (method) {
                    case JavaMetod javaMetod -> executeJavaMethod(span, javaMetod, new Variable[]{valueV}, null, stack);
                    case FluxusMetod fluxusMetod ->
                            executeFluxusMethod(fluxusMetod, new Variable[]{valueV}, null, stack);
                    case TraitMetod _ -> throw new UnsupportedOperationException();
                };
            }
            case Expression.Binary(
                    Span span, Expression left, Expression.BinaryOperator binaryOperator, Expression right
            ) -> {
                Variable leftV = evaluateExpression(left, stack);
                Variable rightV = evaluateExpression(right, stack);
                String methodName = binaryOperator.name().toLowerCase(Locale.ROOT);
                TypeDescriptor type = leftV.descriptor();
                TypeDescriptor[] argumentTypes = {leftV.descriptor(), rightV.descriptor()};
                Metod method = type.findMethod(methodName, argumentTypes, environment)
                        .orElseThrow(() -> new RuntimeException(span + " Method: " + methodName + " with args: " + Arrays.deepToString(argumentTypes) + " not found for class: " + type));
                yield switch (method) {
                    case JavaMetod javaMetod ->
                            executeJavaMethod(span, javaMetod, new Variable[]{leftV, rightV}, null, stack);
                    case FluxusMetod fluxusMetod ->
                            executeFluxusMethod(fluxusMetod, new Variable[]{leftV, rightV}, null, stack);
                    case TraitMetod _ -> throw new UnsupportedOperationException();
                };
            }
            case Expression.ArrayLiteralCreation(_, List<Expression> values) -> {
                Variable[] variables = values.stream()
                        .map((Expression e) -> evaluateExpression(e, stack))
                        .toArray(Variable[]::new);
                TypeDescriptor elementType = variables[0].descriptor();
                yield variableFactory.asArray(variables, elementType);
            }
            case Expression.ArrayCreation(_, Expression defaultValue, Expression length) -> {
                Variable lengthVar = evaluateExpression(length, stack);
                int l = (int) ((U) lengthVar.javaValue()).getValue().getValue();
                Variable d = evaluateExpression(defaultValue, stack);
                Variable[] array = new Variable[l];
                for (int i = 0; i < array.length; i++) {
                    array[i] = d.createNew();
                }
                yield variableFactory.asArray(array, d.descriptor());
            }
            case Expression.ConstructorCall(Span span, Type type, List<Expression> arguments) -> {
                TypeDescriptor typeDescriptor = environment.getKlassRegister().find(type, currentImports)
                        .orElseThrow(() -> new RuntimeException("Class not found for type: " + type));
                Variable[] argumentValues = new Variable[arguments.size()];
                for (int i = 0; i < argumentValues.length; i++) {
                    Expression e = arguments.get(i);
                    Variable variable = evaluateExpression(e, stack);
                    argumentValues[i] = variable;
                }
                yield callConstructor(span, typeDescriptor, argumentValues, stack);
            }
            case Expression.InstanceFieldAccess(_, Expression instance, String fieldName) -> {
                Variable inst = evaluateExpression(instance, stack);
                if (inst instanceof StructVariable struct) {
                    yield struct.getField(fieldName);
                } else {
                    throw new RuntimeException("Instance is not a struct");
                }
            }
            case Expression.StaticFieldAccess(Span span, Type type, String fieldName) -> {
                TypeDescriptor typeDescriptor = environment.getKlassRegister().find(type, currentImports)
                        .orElseThrow(() -> new RuntimeException("Class not found for type: " + type));
                if (!staticStore.has(typeDescriptor))
                    staticStore.init(typeDescriptor, populateStatics(span, typeDescriptor));
                Variable value = staticStore.get(typeDescriptor, fieldName);
                yield Objects.requireNonNull(value);
            }
            case Expression.TypeCast(Span span, Type targetType, Expression valueExpression) -> {
                Variable value = evaluateExpression(valueExpression, stack);
                TypeDescriptor valueType = value.descriptor();
                targetType = targetType.mapNames(currentImports::map);

                String methodName = "as__" + targetType.name().replace('.', '_');
                Metod method = (switch (valueType) {
                    case Trait _, FluxusKlass _ ->
                            valueType.findMethod(methodName, new TypeDescriptor[]{valueType}, environment);
                    case JavaKlass _ ->
                            valueType.findMethod(methodName, new TypeDescriptor[]{valueType, PType.DESCRIPTOR.get()}, environment);
                }).orElseThrow(() -> new RuntimeException("Method: " + methodName + " with args: " + valueType.type().name() + ", " + PType.TYPE.name() + " not found for class: " + valueType));
                yield switch (method) {
                    case JavaMetod javaMetod ->
                            executeJavaMethod(span, javaMetod, new Variable[]{value, new PrimitiveVariable(PType.of(targetType))}, null, stack);
                    case FluxusMetod fluxusMetod ->
                            executeFluxusMethod(fluxusMetod, new Variable[]{value}, null, stack);
                    case TraitMetod _ -> throw new UnsupportedOperationException();
                };
            }
        };
    }

    private Variable callConstructor(Span span, TypeDescriptor typeDescriptor, Variable[] argumentValues, Stack stack) {
        TypeDescriptor[] argumentTypes = new TypeDescriptor[argumentValues.length];
        for (int i = 0; i < argumentTypes.length; i++) {
            argumentTypes[i] = argumentValues[i].descriptor();
        }
        return switch (typeDescriptor) {
            case JavaKlass _ -> throw new NotImplementedException();
            case FluxusKlass fluxusKlass -> {
                FluxusMetod method = fluxusKlass.getConstructor(argumentTypes, environment)
                        .orElseThrow(() -> new RuntimeException("Constructor: with args: " + Arrays.deepToString(argumentTypes) + " not found for class: " + typeDescriptor));
                Variable struct = initStruct(span, fluxusKlass);
                stack.push(new StackFrame(span));
                stack.initializeVariable("this", struct);
                executeFluxusMethod(method, argumentValues, null, stack);
                stack.popDestroy();
                yield struct;
            }
            default -> throw new UnsupportedOperationException();
        };
    }

    private Variable initStruct(Span span, FluxusKlass fluxusKlass) {
        Map<String, Variable> fields = new HashMap<>();
        Stack instanceInitStack = new Stack();
        instanceInitStack.push(new StackFrame(span));
        for (Field field : fluxusKlass.fields()) {
            if (field.isStatic()) continue;
            String name = field.name();
            Variable fieldInit = evaluateExpression(field.init(), instanceInitStack);
            fields.put(name, fieldInit);
            instanceInitStack.initializeVariable(name, fieldInit);
        }
        return new StructVariable(fluxusKlass, fields);
    }

    private Map<String, Variable> populateStatics(Span span, TypeDescriptor typeDescriptor) {
        Map<String, Variable> variables = new HashMap<>();
        Stack staticInitStack = new Stack();
        staticInitStack.push(new StackFrame(span));
        if (typeDescriptor instanceof FluxusKlass klass) {
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

    private Variable executeJavaMethod(Span span, JavaMetod javaMetod, Variable[] argumentValues, @Nullable Variable instance, Stack stack) {
        Object result;
        try {
            Method method = javaMetod.javaMethod();
            Object javaInstance = null;
            if (!javaMetod.isStatic() && instance != null) {
                javaInstance = instance.javaValue();
            }
            Object[] javaArgs = new Object[argumentValues.length];
            for (int i = 0; i < javaArgs.length; i++) {
                javaArgs[i] = argumentValues[i].javaValue();
            }
            if (javaMetod.needsNatives()) {
                Object[] args = new Object[javaArgs.length + 1];
                args[0] = environment.getNatives();
                System.arraycopy(javaArgs, 0, args, 1, javaArgs.length);
                result = method.invoke(javaInstance, args);
            } else
                result = method.invoke(javaInstance, javaArgs);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            switch (e.getCause()) {
                case PanicException pe ->
                        throw new InterpreterException.Panic(span, stack, variableFactory.getLiteralStore(), pe.getMessage());
                case ArrayIndexOutOfBoundsException ae when argumentValues.length != 0 && argumentValues[0] instanceof PrimitiveVariable primitiveVariable && primitiveVariable.javaValue() instanceof Array ->
                        throw new InterpreterException.CollectionAccessOutOfBounds(span, stack, variableFactory.getLiteralStore(), ae.getMessage());
                case CastNegativeToUnsignedException ce ->
                        throw new InterpreterException.CastNegativeToUnsigned(span, stack, variableFactory.getLiteralStore(), ce.getMessage());
                case StringIndexOutOfBoundsException se ->
                        throw new InterpreterException.CastNegativeToUnsigned(span, stack, variableFactory.getLiteralStore(), se.getMessage());
                case null, default -> throw new RuntimeException(span.toString(), e);
            }
        }

        if (result instanceof String s)
            return createString(stack, span, s);
        else
            return variableFactory.fromJavaValue(result);
    }

    private Variable createString(Stack stack, Span span, String value) {
        Variable array = variableFactory.asArray(value.chars()
                .mapToObj(c -> variableFactory.fromCharLiteral(String.valueOf((char) c)))
                .toArray(Variable[]::new), Char.DESCRIPTOR.get());
        TypeDescriptor typeDescriptor = environment.getKlassRegister().find(new Type.Simple(span, "fluxus.flumen.Str"), currentImports)
                .orElseThrow(() -> new RuntimeException("Str class not found"));
        return callConstructor(span, typeDescriptor, new Variable[]{array}, stack);
    }

    private Variable executeFluxusMethod(FluxusMetod fluxusMetod, Variable[] argumentValues, @Nullable Variable instance, Stack stack) {
        stack.push(new StackFrame(fluxusMetod.body().span()));
        if (instance != null) stack.initializeVariable("this", instance);
        String[] parameterNames = fluxusMetod.parameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            stack.initializeVariable(parameterNames[i], argumentValues[i]);
        }
        try {
            executeStatement(fluxusMetod.body(), stack);
        } catch (ReturnException returnException) {
            StackFrame _ = stack.popDestroy();
            return returnException.getReturnValue();
        }
        StackFrame _ = stack.popDestroy();
        return variableFactory.fromJavaValue(null);
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
        interruptStack.push(new StackFrame(interrupt.unterbrechungsbehandlungsprozedur().span()));
        try {
            executeStatement(interrupt.unterbrechungsbehandlungsprozedur(), interruptStack);
        } catch (ReturnException re) {
            interruptStack.popDestroy();

            runningInterrupts.remove(interrupt.mutexID());

            currentImports = oldImports;
            throw re;
        }

        interruptStack.popDestroy();

        runningInterrupts.remove(interrupt.mutexID());

        currentImports = oldImports;
    }

}
