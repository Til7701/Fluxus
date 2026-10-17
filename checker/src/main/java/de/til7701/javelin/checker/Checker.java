package de.til7701.javelin.checker;

import module de.til7701.javelin.ast;
import de.til7701.javelin.checker.internal.Scope;
import de.til7701.javelin.checker.internal.ScopeStack;
import de.til7701.javelin.common.NotImplementedException;
import de.til7701.javelin.common.environment.Environment;
import de.til7701.javelin.common.environment.Imports;
import de.til7701.javelin.common.shaft.Bool;
import de.til7701.javelin.common.shaft.Str;
import de.til7701.javelin.common.type.Metod;
import de.til7701.javelin.common.type.TypeDescriptor;
import de.til7701.javelin.common.type.klass.*;
import de.til7701.javelin.common.type.trait.CollectionTrait;
import de.til7701.javelin.common.type.trait.ITrait;
import de.til7701.javelin.common.type.trait.Trait;
import de.til7701.javelin.common.type.trait.UTrait;
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
                TypeDescriptor expressionType = evaluateExpressionType(value, stack, imports);
                if (expressionType != null)
                    stack.initializeVariable(name, expressionType);
            }
            case Assignment(Span span, Expression target, Expression value) -> {
                TypeDescriptor expressionType = evaluateExpressionType(value, stack, imports);
                TypeDescriptor targetType = evaluateExpressionType(target, stack, imports);
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
                TypeDescriptor conditionType = evaluateExpressionType(condition, stack, imports);
                if (conditionType != null)
                    checkAssignability(whenStatement.span(), conditionType, Bool.DESCRIPTOR.get());
                stack.push(new Scope());
                check(whenStatement.body(), stack, imports);
                stack.pop();
            }
            case Expression expression -> {
                TypeDescriptor _ = evaluateExpressionType(expression, stack, imports);
            }
        }
    }

    private @Nullable TypeDescriptor evaluateExpressionType(Expression expression, ScopeStack stack, Imports imports) {
        return switch (expression) {
            case NewExpression(_, Expression e) -> evaluateExpressionType(e, stack, imports);
            case BooleanLiteralExpression _ -> Bool.DESCRIPTOR.get();
            case SignedIntegerLiteralExpression(_, _, long bitCount) -> new ITrait(bitCount);
            case UnsignedIntegerLiteralExpression(_, _, long bitCount) -> new UTrait(bitCount);
            case StringLiteralExpression _ -> Str.DESCRIPTOR.get();
            case SymbolExpression(Span span, String identifier) -> {
                TypeDescriptor type = stack.getVariable(identifier);
                if (type == null)
                    errors.add(new Error.SymbolNotFound(span, identifier));
                yield type;
            }
            case InstanceMethodCall(Span span, Expression instance, String methodName, List<Expression> arguments) -> {
                TypeDescriptor instanceType = evaluateExpressionType(instance, stack, imports);
                if (instanceType != null)
                    yield evaluateMethodCall(span, instanceType, methodName, arguments, stack, imports);
                yield null;
            }
            case StaticMethodCall(Span span, Type type, String methodName, List<Expression> arguments) -> {
                TypeDescriptor typeDescriptor = findTypeDescriptor(span, type.mapNames(imports::map), imports);
                if (typeDescriptor == null) yield null;
                yield evaluateMethodCall(span, typeDescriptor, methodName, arguments, stack, imports);
            }
            case ConstructorCall(Span span, Type type, List<Expression> arguments) -> {
                type = type.mapNames(imports::map);
                TypeDescriptor typeDescriptor = findTypeDescriptor(span, type, imports);
                if (typeDescriptor == null) yield null;
                TypeDescriptor[] argumentTypes = new TypeDescriptor[arguments.size()];
                for (int i = 0; i < arguments.size(); i++) {
                    TypeDescriptor t = evaluateExpressionType(arguments.get(i), stack, imports);
                    if (t == null) yield null;
                    argumentTypes[i] = t;
                }
                switch (typeDescriptor) {
                    case JavaKlass _ -> throw new NotImplementedException();
                    case JavelinKlass javelinKlass -> {
                        JavelinMetod method = findConstructor(span, javelinKlass, argumentTypes);
                        if (method == null) yield null;

                        TypeDescriptor[] parameterDescriptors = toDescriptors(method.parameterTypes(), imports);
                        if (parameterDescriptors == null) yield null;
                        checkAssignability(span, argumentTypes, parameterDescriptors);

                        yield typeDescriptor;
                    }
                    case Trait trait -> {
                        errors.add(new Error.Other(span, "Trait " + trait + " cannot be instantiated"));
                        yield null;
                    }
                }
            }
            case BinaryExpression(Span span, Expression left, BinaryOperator operator, Expression right) -> {
                TypeDescriptor leftT = evaluateExpressionType(left, stack, imports);
                if (leftT == null) yield null;
                TypeDescriptor rightT = evaluateExpressionType(right, stack, imports);
                if (rightT == null) yield null;
                String methodName = operator.name().toLowerCase(Locale.ROOT);
                yield evaluateMethodCall(span, leftT, methodName, List.of(left, right), stack, imports);
            }
            case ArrayLiteralCreation(Span span, List<Expression> values) -> {
                TypeDescriptor[] types = new TypeDescriptor[values.size()];
                for (int i = 0; i < types.length; i++) {
                    TypeDescriptor t = evaluateExpressionType(values.get(i), stack, imports);
                    if (t == null) yield null;
                    types[i] = t;
                }
                if (types.length > 1) {
                    TypeDescriptor first = types[0];
                    for (int i = 1; i < types.length; i++) {
                        TypeDescriptor other = types[i];
                        checkAssignability(span, other, first);
                    }
                }
                yield new CollectionTrait(types[0], new UTrait(32));
            }
            case InstanceFieldAccess(Span span, Expression instance, String fieldName) -> {
                TypeDescriptor instanceType = evaluateExpressionType(instance, stack, imports);
                if (instanceType == null) yield null;
                yield switch (instanceType) {
                    case JavaKlass _ -> null;
                    case JavelinKlass javelinKlass -> {
                        Optional<Field> field = javelinKlass.fields().stream()
                                .filter(f -> f.name().equals(fieldName))
                                .findAny();
                        if (field.isEmpty()) yield null;
                        yield findTypeDescriptor(span, field.get().type(), imports);
                    }
                    case Trait _ -> {
                        errors.add(new Error.Other(span, "Trait field access"));
                        yield null;
                    }
                };
            }
            case TypeCastExpression(Span span, Type targetType, Expression valueExpression) -> {
                targetType = targetType.mapNames(imports::map);
                Optional<TypeDescriptor> targetDescriptor = environment.getKlassRegister().find(targetType, imports);
                if (targetDescriptor.isEmpty()) yield null;
                TypeDescriptor valueType = evaluateExpressionType(valueExpression, stack, imports);
                if (valueType == null) yield null;
                String methodName = "as__" + targetType.name().replace('.', '_');
                Metod metod = findMetod(span, valueType, methodName, new TypeDescriptor[]{valueType});
                if (metod == null) yield null;
                Optional<TypeDescriptor> returnDescriptor = environment.getKlassRegister().find(metod.returnType(), imports);
                if (returnDescriptor.isEmpty()) yield null;
                checkAssignability(span, returnDescriptor.get(), targetDescriptor.get());
                yield targetDescriptor.get();
            }
            default -> throw new NotImplementedException();
        };
    }

    private @Nullable TypeDescriptor evaluateMethodCall(Span span, TypeDescriptor type, String methodName, List<Expression> arguments,
                                                        ScopeStack stack, Imports imports) {
        TypeDescriptor[] argumentTypes = new TypeDescriptor[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            TypeDescriptor t = evaluateExpressionType(arguments.get(i), stack, imports);
            if (t == null) return null;
            argumentTypes[i] = t;
        }
        Metod method = findMetod(span, type, methodName, argumentTypes);
        if (method == null) return null;

        TypeDescriptor[] parameterDescriptors = toDescriptors(method.parameterTypes(), imports);
        if (parameterDescriptors == null) return null;

        checkAssignability(span, argumentTypes, parameterDescriptors);

        return environment.getKlassRegister().find(method.returnType(), imports).orElse(null);
    }

    private @Nullable TypeDescriptor findTypeDescriptor(Span span, Type type, Imports imports) {
        Optional<TypeDescriptor> typeDescriptor = environment.getKlassRegister().find(type, imports);
        if (typeDescriptor.isEmpty()) {
            errors.add(new Error.KlassNotFound(span, type));
            return null;
        }
        return typeDescriptor.get();
    }

    private @Nullable Metod findMetod(Span span, TypeDescriptor typeDescriptor, String methodName, TypeDescriptor[] arguments) {
        Optional<Metod> metod = switch (typeDescriptor) {
            case Klass klass -> klass.findMethod(methodName, arguments, environment);
            case Trait trait -> trait.findMethod(methodName, arguments, environment).map(m -> (Metod) m);
        };
        if (metod.isEmpty()) {
            errors.add(new Error.MethodNotFound(span, methodName, arguments, typeDescriptor));
            return null;
        }
        return metod.get();
    }

    private @Nullable JavelinMetod findConstructor(Span span, JavelinKlass klass, TypeDescriptor[] arguments) {
        Optional<JavelinMetod> constructor = klass.getConstructor(arguments, environment);
        if (constructor.isEmpty()) {
            errors.add(new Error.MethodNotFound(span, "", arguments, klass));
            return null;
        }
        return constructor.get();
    }

    private void checkAssignability(Span span, TypeDescriptor from, TypeDescriptor to) {
        if (!environment.isAssignable(from, to)) {
            errors.add(new Error.TypeMismatch(span, from, to));
        }
    }

    private void checkAssignability(Span span, TypeDescriptor[] from, TypeDescriptor[] to) {
        for (int i = 0; i < from.length; i++) {
            checkAssignability(span, from[i], to[i]);
        }
    }

    private TypeDescriptor @Nullable [] toDescriptors(Type[] types, Imports imports) {
        TypeDescriptor[] typeDescriptors = new TypeDescriptor[types.length];
        for (int i = 0; i < types.length; i++) {
            Optional<TypeDescriptor> t = environment.getKlassRegister().find(types[i], imports);
            if (t.isEmpty()) return null;
            typeDescriptors[i] = t.get();
        }
        return typeDescriptors;
    }

}
