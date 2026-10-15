package de.til7701.javelin.checker;

import module de.til7701.javelin.ast;
import de.til7701.javelin.checker.internal.Scope;
import de.til7701.javelin.checker.internal.ScopeStack;
import de.til7701.javelin.common.NotImplementedException;
import de.til7701.javelin.common.environment.Environment;
import de.til7701.javelin.common.environment.Imports;
import de.til7701.javelin.common.klass.Klass;
import de.til7701.javelin.common.klass.Metod;
import de.til7701.javelin.common.shaft.Bool;
import de.til7701.javelin.common.shaft.Str;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Checker {

    private final Environment environment;

    @Getter
    private final List<TypeMismatch> mismatches = new ArrayList<>();
    @Getter
    private final List<SymbolNotFound> symbolNotFounds = new ArrayList<>();
    @Getter
    private final List<MethodNotFound> methodNotFounds = new ArrayList<>();
    @Getter
    private final List<KlassNotFound> klassNotFounds = new ArrayList<>();

    public Checker(Environment environment) {
        this.environment = environment;
    }

    private void reset() {
        mismatches.clear();
    }

    public void check(Ast ast) {
        reset();

        ScopeStack stack = new ScopeStack();
        stack.push(new Scope());
        Imports imports = new Imports();
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
                    symbolNotFounds.add(new SymbolNotFound(span, identifier));
                yield type;
            }
            case InstanceMethodCall(Span span, Expression instance, String methodName, List<Expression> arguments) -> {
                Type instanceType = evaluateExpressionType(instance, stack, imports);
                if (instanceType != null)
                    yield evaluateMethodCall(span, instanceType, methodName, arguments, stack, imports);
                yield null;
            }
            case StaticMethodCall(Span span, Type type, String methodName, List<Expression> arguments) ->
                    evaluateMethodCall(span, type, methodName, arguments, stack, imports);
//            case ConstructorCall(Span span, Type type, List<Expression> arguments) -> {
//
//            }
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
            klassNotFounds.add(new KlassNotFound(span, type));
            return null;
        }
        return klass.get();
    }

    private @Nullable Metod findMetod(Span span, Klass klass, String methodName, Type[] arguments) {
        Optional<Metod> metod = klass.getMethod(methodName, arguments, environment);
        if (metod.isEmpty()) {
            methodNotFounds.add(new MethodNotFound(span, methodName, arguments, klass));
            return null;
        }
        return metod.get();
    }

    private void checkAssignability(Span span, Type from, Type to) {
        if (!to.isAssignableTo(from)) {
            mismatches.add(new TypeMismatch(span, from, to));
        }
    }

    private void checkAssignability(Span span, Type[] from, Type[] to) {
        for (int i = 0; i < from.length; i++) {
            checkAssignability(span, from[i], to[i]);
        }
    }

}
