package de.til7701.fluxus.common.checker;

import module de.til7701.fluxus.ast;
import de.til7701.fluxus.common.NotImplementedException;
import de.til7701.fluxus.common.checker.internal.Scope;
import de.til7701.fluxus.common.checker.internal.ScopeStack;
import de.til7701.fluxus.common.environment.Environment;
import de.til7701.fluxus.common.environment.Imports;
import de.til7701.fluxus.common.fons.Bool;
import de.til7701.fluxus.common.fons.Char;
import de.til7701.fluxus.common.fons.PType;
import de.til7701.fluxus.common.type.Metod;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.type.klass.Field;
import de.til7701.fluxus.common.type.klass.FluxusKlass;
import de.til7701.fluxus.common.type.klass.FluxusMetod;
import de.til7701.fluxus.common.type.klass.JavaKlass;
import de.til7701.fluxus.common.type.trait.*;
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

    public void check(Ast ast, String fullyQualifiedFluxusName) {
        reset();

        Imports imports = environment.getPrimitiveImports();
        switch (ast) {
            case Script script -> {
                ScopeStack stack = new ScopeStack();
                stack.push(new Scope());
                for (Statement s : script.statements()) {
                    check(s, stack, imports);
                }
            }
            case TypeDefinition.ClassDefinition classDefinition -> {
                imports.addImport(new Statement.Import(Span.undefined(), fullyQualifiedFluxusName, null));
                imports.addImport(new Statement.Import(Span.undefined(), fullyQualifiedFluxusName, "This"));
                for (Statement.Import i : classDefinition.imports())
                    imports.addImport(i);

                ScopeStack staticFieldsStack = new ScopeStack();
                staticFieldsStack.push(new Scope());
                for (TypeDefinition.ClassFieldDefinition field : classDefinition.fields()) {
                    if (field.modifiers().stream().anyMatch(m -> m.value() != TypeDefinition.FieldModifierValue.STATIC))
                        continue;
                    TypeDescriptor fieldType = findTypeDescriptor(field.span(), field.type(), imports);
                    if (fieldType == null) continue;
                    TypeDescriptor valueType = evaluateExpressionType(field.value(), staticFieldsStack, imports);
                    if (valueType == null) continue;
                    checkAssignability(field.span(), valueType, fieldType);
                    staticFieldsStack.initializeVariable(field.name(), fieldType);
                }

                for (TypeDefinition.ClassFieldDefinition field : classDefinition.fields()) {
                    if (field.modifiers().stream().anyMatch(m -> m.value() == TypeDefinition.FieldModifierValue.STATIC))
                        continue;
                    TypeDescriptor fieldType = findTypeDescriptor(field.span(), field.type(), imports);
                    if (fieldType == null) continue;
                    ScopeStack stack = new ScopeStack();
                    stack.push(new Scope());
                    TypeDescriptor valueType = evaluateExpressionType(field.value(), stack, imports);
                    if (valueType == null) continue;
                    checkAssignability(field.span(), valueType, fieldType);
                }

                for (TypeDefinition.ConstructorDefinition constructorDefinition : classDefinition.constructors()) {
                    checkMethod(false, constructorDefinition.parameters().parameters(), constructorDefinition.body(), imports);
                }

                for (TypeDefinition.MethodDefinition methodDefinition : classDefinition.methods()) {
                    checkMethod(
                            methodDefinition.modifiers().stream().anyMatch(m -> m.value() == TypeDefinition.MethodModifierValue.STATIC),
                            methodDefinition.parameters().parameters(), methodDefinition.body(), imports
                    );
                }
            }
            case TypeDefinition.TemplateDefinition _ ->
                    throw new IllegalArgumentException("Templates cannot be checked");
            case TypeDefinition.TemplateInstance _ ->
                    throw new IllegalArgumentException("Uninstantiated template instances cannot be checked");
        }
    }

    private void checkMethod(boolean isStatic, List<TypeDefinition.MethodParameter> parameters, Statement body, Imports imports) {
        ScopeStack stack = new ScopeStack();
        stack.push(new Scope());
        if (!isStatic) {
            TypeDescriptor thisType = findTypeDescriptor(Span.undefined(), new Type.Simple(Span.undefined(), "This"), imports);
            if (thisType == null) return;
            stack.initializeVariable("this", thisType);
        }
        for (TypeDefinition.MethodParameter parameter : parameters) {
            TypeDescriptor t = findTypeDescriptor(parameter.span(), parameter.type(), imports);
            if (t == null)
                return;
            stack.initializeVariable(parameter.name(), t);
        }
        check(body, stack, imports);
    }

    private void check(Statement statement, ScopeStack stack, Imports imports) {
        switch (statement) {
            case Statement.Import i -> imports.addImport(i);
            case Statement.VariableInitialization(_, String name, Expression value) -> {
                TypeDescriptor expressionType = evaluateExpressionType(value, stack, imports);
                if (expressionType != null)
                    stack.initializeVariable(name, expressionType);
            }
            case Statement.Assignment(Span span, Expression target, Expression value) -> {
                TypeDescriptor expressionType = evaluateExpressionType(value, stack, imports);
                TypeDescriptor targetType = evaluateExpressionType(target, stack, imports);
                if (expressionType != null && targetType != null)
                    checkAssignability(span, expressionType, targetType);
            }
            case Statement.Return(Span span, Expression value) -> {
                // TODO
            }
            case Statement.StatementList statementList -> {
                stack.push(new Scope());
                for (Statement s : statementList.statements()) {
                    check(s, stack, imports);
                }
                stack.pop();
            }
            case Statement.When whenStatement -> {
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
            case Expression.New(_, Expression e) -> evaluateExpressionType(e, stack, imports);
            case Expression.BooleanLiteral _ -> Bool.DESCRIPTOR.get();
            case Expression.SignedIntegerLiteral(_, _, long bitCount) -> new ITrait(bitCount);
            case Expression.UnsignedIntegerLiteral(_, _, long bitCount) -> new UTrait(bitCount);
            case Expression.FloatLiteral(_, _, long bitCount) -> new FTrait(bitCount);
            case Expression.CharLiteral _ -> Char.DESCRIPTOR.get();
            case Expression.StringLiteral _ -> environment.getKlassRegister()
                    .find(new Type.Simple(Span.undefined(), "fluxus.flumen.Str"), imports)
                    .orElse(null);
            case Expression.Symbol(Span span, String identifier) -> {
                TypeDescriptor type = stack.getVariable(identifier);
                if (type == null)
                    errors.add(new Error.SymbolNotFound(span, identifier));
                yield type;
            }
            case Expression.InstanceMethodCall(
                    Span span, Expression instance, String methodName, List<Expression> arguments
            ) -> {
                TypeDescriptor instanceType = evaluateExpressionType(instance, stack, imports);
                if (instanceType != null)
                    yield evaluateMethodCall(span, instanceType, methodName, arguments, stack, imports);
                yield null;
            }
            case Expression.StaticMethodCall(Span span, Type type, String methodName, List<Expression> arguments) -> {
                TypeDescriptor typeDescriptor = findTypeDescriptor(span, type.mapNames(imports::map), imports);
                if (typeDescriptor == null) yield null;
                yield evaluateMethodCall(span, typeDescriptor, methodName, arguments, stack, imports);
            }
            case Expression.ConstructorCall(Span span, Type type, List<Expression> arguments) -> {
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
                    case FluxusKlass fluxusKlass -> {
                        FluxusMetod method = findConstructor(span, fluxusKlass, argumentTypes);
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
            case Expression.LeftUnary(Span span, Expression.LeftUnaryOperator operator, Expression value) -> {
                TypeDescriptor valueT = evaluateExpressionType(value, stack, imports);
                if (valueT == null) yield null;
                String methodName = operator.name().toLowerCase(Locale.ROOT);
                yield evaluateMethodCall(span, valueT, methodName, List.of(value), stack, imports);
            }
            case Expression.Binary(
                    Span span, Expression left, Expression.BinaryOperator operator, Expression right
            ) -> {
                TypeDescriptor leftT = evaluateExpressionType(left, stack, imports);
                if (leftT == null) yield null;
                TypeDescriptor rightT = evaluateExpressionType(right, stack, imports);
                if (rightT == null) yield null;
                String methodName = operator.name().toLowerCase(Locale.ROOT);
                yield evaluateMethodCall(span, leftT, methodName, List.of(left, right), stack, imports);
            }
            case Expression.ArrayLiteralCreation(Span span, List<Expression> values) -> {
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
                yield new CollectionTrait(types[0], new UTrait(31));
            }
            case Expression.ArrayCreation(_, Expression defaultValue, Expression length) -> {
                TypeDescriptor defaultType = evaluateExpressionType(defaultValue, stack, imports);
                if (defaultType == null)
                    yield null;
                TypeDescriptor lengthType = evaluateExpressionType(length, stack, imports);
                if (lengthType != null)
                    checkAssignability(length.span(), lengthType, new UTrait(31));
                yield new CollectionTrait(defaultType, new UTrait(31));
            }
            case Expression.InstanceFieldAccess(Span span, Expression instance, String fieldName) -> {
                TypeDescriptor instanceType = evaluateExpressionType(instance, stack, imports);
                if (instanceType == null) yield null;
                yield switch (instanceType) {
                    case JavaKlass _ -> null;
                    case FluxusKlass fluxusKlass -> {
                        Optional<Field> field = fluxusKlass.fields().stream()
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
            case Expression.StaticFieldAccess(Span span, Type type, String fieldName) -> {
                TypeDescriptor typeDescriptor = findTypeDescriptor(span, type, imports);
                if (typeDescriptor == null) yield null;
                Field field = switch (typeDescriptor) {
                    case FluxusKlass klass -> klass.fields().stream()
                            .filter(f -> f.name().equals(fieldName))
                            .findAny()
                            .orElse(null);
                    case JavaKlass _, Trait _ -> null;
                };
                if (field == null) yield null;
                yield findTypeDescriptor(span, field.type(), imports);
            }
            case Expression.TypeCast(Span span, Type targetType, Expression valueExpression) -> {
                targetType = targetType.mapNames(imports::map);
                TypeDescriptor targetDescriptor = findTypeDescriptor(span, targetType, imports);
                if (targetDescriptor == null) yield null;
                TypeDescriptor valueType = evaluateExpressionType(valueExpression, stack, imports);
                if (valueType == null) yield null;
                String methodName = "as__" + targetType.name().replace('.', '_');
                Metod metod = switch (valueType) {
                    case CollectionTrait _, FluxusKlass _ ->
                            findMetod(span, valueType, methodName, new TypeDescriptor[]{valueType});
                    case ITrait _, UTrait _, FTrait _, JavaKlass _ ->
                            findMetod(span, valueType, methodName, new TypeDescriptor[]{valueType, PType.DESCRIPTOR.get()});
                };
                if (metod == null) yield null;
                TypeDescriptor returnDescriptor = findTypeDescriptor(span, metod.returnType(), imports);
                if (returnDescriptor == null) yield null;
                checkAssignability(span, returnDescriptor, targetDescriptor);
                yield targetDescriptor;
            }
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
        Optional<Metod> metod = typeDescriptor.findMethod(methodName, arguments, environment);
        if (metod.isEmpty()) {
            errors.add(new Error.MethodNotFound(span, methodName, arguments, typeDescriptor));
            return null;
        }
        return metod.get();
    }

    private @Nullable FluxusMetod findConstructor(Span span, FluxusKlass klass, TypeDescriptor[] arguments) {
        Optional<FluxusMetod> constructor = klass.getConstructor(arguments, environment);
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
