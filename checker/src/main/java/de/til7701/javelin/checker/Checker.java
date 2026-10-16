package de.til7701.javelin.checker;

import module de.til7701.javelin.ast;
import de.til7701.javelin.checker.internal.Scope;
import de.til7701.javelin.checker.internal.ScopeStack;
import de.til7701.javelin.common.NotImplementedException;
import de.til7701.javelin.common.environment.Environment;
import de.til7701.javelin.common.environment.Imports;
import de.til7701.javelin.common.klass.*;
import de.til7701.javelin.common.shaft.Bool;
import de.til7701.javelin.common.shaft.Str;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Checker {

    private final Environment environment;

    @Getter
    private final List<Error> errors = new ArrayList<>();

    public Checker(Environment environment) {
        this.environment = environment;
    }

    private void reset() {
        errors.clear();
    }

    public void check(Ast ast) {
        reset();

        ScopeStack stack = new ScopeStack();
        stack.push(new Scope());
        Imports imports = environment.getPrimitiveImports();
        switch (ast) {
            case Script script -> script.statements().forEach(s -> check(s, stack, imports));
            case TypeDefinition typeDefinition -> {
                // TODO
            }
        }
    }

    private void check(Statement statement, ScopeStack stack, Imports imports) {
        switch (statement) {
            case Import i -> imports.addImport(i);
            case VariableInitialization(_, String name, Expression value) -> {
                Type expressionType = evaluateExpressionType(value, stack, imports);
                if (expressionType != null)
                    stack.initializeVariable(name, expressionType);
            }
            case Assignment(Span span, Expression target, Expression value) -> {
                Type expressionType = evaluateExpressionType(value, stack, imports);
                Type targetType = evaluateExpressionType(target, stack, imports);
                if (expressionType != null && targetType != null)
                    checkAssignability(span, expressionType, targetType);
            }
            case ReturnStatement(Span span, Expression value) -> {
                // TODO
            }
            case StatementList statementList -> {
                stack.push(new Scope());
                statementList.statements().forEach(s -> check(s, stack, imports));
                stack.pop();
            }
            case WhenStatement whenStatement -> {
                Expression condition = whenStatement.condition();
                Type conditionType = evaluateExpressionType(condition, stack, imports);
                if (conditionType != null)
                    checkAssignability(whenStatement.span(), conditionType, Bool.TYPE);
                stack.push(new Scope());
                check(whenStatement.body(), stack, imports);
                stack.pop();
            }
            case Expression expression -> {
                Type _ = evaluateExpressionType(expression, stack, imports);
            }
        }
    }

    private @Nullable Type evaluateExpressionType(Expression expression, ScopeStack stack, Imports imports) {
        return switch (expression) {
            case NewExpression(_, Expression e) -> evaluateExpressionType(e, stack, imports);
            case BooleanLiteralExpression _ -> Bool.TYPE;
            case SignedIntegerLiteralExpression(Span span, _, long bitCount) -> new IType(span, (int) bitCount);
            case UnsignedIntegerLiteralExpression(Span span, _, long bitCount) -> new UType(span, (int) bitCount);
            case StringLiteralExpression _ -> Str.TYPE;
            case SymbolExpression(Span span, String identifier) -> {
                Type type = stack.getVariable(identifier);
                if (type == null)
                    errors.add(new Error.SymbolNotFound(span, identifier));
                yield type;
            }
            case InstanceMethodCall(Span span, Expression instance, String methodName, List<Expression> arguments) -> {
                Type instanceType = evaluateExpressionType(instance, stack, imports);
                if (instanceType != null)
                    yield evaluateMethodCall(span, instanceType, methodName, arguments, stack, imports);
                yield null;
            }
            case StaticMethodCall(Span span, Type type, String methodName, List<Expression> arguments) ->
                    evaluateMethodCall(span, type.mapNames(imports::map), methodName, arguments, stack, imports);
            case ConstructorCall(Span span, Type type, List<Expression> arguments) -> {
                type = type.mapNames(imports::map);
                Klass klass = findKlass(span, type, imports);
                if (klass == null) yield null;
                Type[] argumentTypes = new Type[arguments.size()];
                for (int i = 0; i < arguments.size(); i++) {
                    Type t = evaluateExpressionType(arguments.get(i), stack, imports);
                    if (t == null) yield null;
                    argumentTypes[i] = t;
                }
                switch (klass) {
                    case JavaKlass _ -> throw new NotImplementedException();
                    case JavelinKlass javelinKlass -> {
                        JavelinMetod method = findConstructor(span, javelinKlass, argumentTypes);
                        if (method == null) yield null;

                        checkAssignability(span, argumentTypes, method.parameterTypes());

                        yield type;
                    }
                }
            }
            case BinaryExpression(Span span, Expression left, BinaryOperator operator, Expression right) -> {
                Type leftT = evaluateExpressionType(left, stack, imports);
                if (leftT == null) yield null;
                Type rightT = evaluateExpressionType(right, stack, imports);
                if (rightT == null) yield null;
                String methodName = operator.name().toLowerCase(Locale.ROOT);
                yield evaluateMethodCall(span, leftT, methodName, List.of(left, right), stack, imports);
            }
            case ArrayLiteralCreation(Span span, List<Expression> values) -> {
                Type[] types = new Type[values.size()];
                for (int i = 0; i < types.length; i++) {
                    Type t = evaluateExpressionType(values.get(i), stack, imports);
                    if (t == null) yield null;
                    types[i] = t;
                }
                if (types.length > 1) {
                    Type first = types[0];
                    for (int i = 1; i < types.length; i++) {
                        Type other = types[i];
                        checkAssignability(span, other, first);
                    }
                }
                yield new CollectionType(span, new UType(Span.undefined(), 32), types[0]);
            }
            case InstanceFieldAccess(Span span, Expression instance, String fieldName) -> {
                Type instanceType = evaluateExpressionType(instance, stack, imports);
                if (instanceType == null) yield null;
                Klass klass = findKlass(span, instanceType, imports);
                if (klass == null) yield null;
                yield switch (klass) {
                    case JavaKlass _ -> null;
                    case JavelinKlass javelinKlass -> {
                        Optional<KlassField> field = javelinKlass.fields().stream()
                                .filter(f -> f.name().equals(fieldName))
                                .findAny();
                        if (field.isEmpty()) yield null;
                        yield field.get().type();
                    }
                };
            }
            case TypeCastExpression(Span span, Type targetType, Expression valueExpression) -> {
                targetType = targetType.mapNames(imports::map);
                Type valueType = evaluateExpressionType(valueExpression, stack, imports);
                if (valueType == null) yield null;
                Klass klass = findKlass(span, valueType, imports);
                if (klass == null) yield null;
                String methodName = "as__" + targetType.name().replace('.', '_');
                Metod metod = findMetod(span, klass, methodName, new Type[]{valueType});
                if (metod == null) yield null;
                checkAssignability(span, metod.returnType(), targetType);
                yield targetType;
            }
            default -> throw new NotImplementedException();
        };
    }

    private @Nullable Type evaluateMethodCall(Span span, Type type, String methodName, List<Expression> arguments,
                                              ScopeStack stack, Imports imports) {
        Klass klass = findKlass(span, type, imports);
        if (klass == null) return null;
        Type[] argumentTypes = new Type[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            Type t = evaluateExpressionType(arguments.get(i), stack, imports);
            if (t == null) return null;
            argumentTypes[i] = t;
        }
        Metod method = findMetod(span, klass, methodName, argumentTypes);
        if (method == null) return null;

        checkAssignability(span, argumentTypes, method.parameterTypes());

        return method.returnType();
    }

    private @Nullable Klass findKlass(Span span, Type type, Imports imports) {
        Optional<Klass> klass = environment.getKlassRegister().getKlass(type, imports);
        if (klass.isEmpty()) {
            errors.add(new Error.KlassNotFound(span, type));
            return null;
        }
        return klass.get();
    }

    private @Nullable Metod findMetod(Span span, Klass klass, String methodName, Type[] arguments) {
        Optional<Metod> metod = klass.getMethod(methodName, arguments, environment);
        if (metod.isEmpty()) {
            errors.add(new Error.MethodNotFound(span, methodName, arguments, klass));
            return null;
        }
        return metod.get();
    }

    private @Nullable JavelinMetod findConstructor(Span span, JavelinKlass klass, Type[] arguments) {
        Optional<JavelinMetod> constructor = klass.getConstructor(arguments, environment);
        if (constructor.isEmpty()) {
            errors.add(new Error.MethodNotFound(span, "", arguments, klass));
            return null;
        }
        return constructor.get();
    }

    private void checkAssignability(Span span, Type from, Type to) {
        if (!to.isAssignableTo(from)) {
            errors.add(new Error.TypeMismatch(span, from, to));
        }
    }

    private void checkAssignability(Span span, Type[] from, Type[] to) {
        for (int i = 0; i < from.length; i++) {
            checkAssignability(span, from[i], to[i]);
        }
    }

}
