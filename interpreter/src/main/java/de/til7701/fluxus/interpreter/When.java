package de.til7701.fluxus.interpreter;

import de.til7701.fluxus.ast.Expression;
import de.til7701.fluxus.ast.Statement;
import de.til7701.fluxus.common.NotImplementedException;
import de.til7701.fluxus.common.environment.Imports;
import de.til7701.fluxus.common.fons.Bool;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.util.*;

final class When implements VariableObserver {

    private final Interpreter interpreter;
    private final long interruptMutexID;
    private final Stack stack;
    private final Expression condition;
    private final Statement body;
    private final Imports imports;
    private final Collection<Variable> topVariables;
    @Getter
    private final StackFrame creationFrame;

    When(Interpreter interpreter, long interruptMutexID, boolean evalInstantly, Stack stack, Expression condition, Statement body, Imports imports) {
        this.interpreter = interpreter;
        this.interruptMutexID = interruptMutexID;
        this.stack = stack;
        this.condition = condition;
        this.body = body;
        this.imports = imports;
        this.creationFrame = stack.peek();

        topVariables = topVariables(condition, stack);
        topVariables.forEach(v -> v.listen(this));
        if (evalInstantly) run(null);
    }

    private Set<Variable> topVariables(Expression expression, Stack stack) {
        return switch (expression) {
            case Expression.Symbol(_, String name) -> Set.of(Objects.requireNonNull(stack.getVariable(name)));
            case Expression.BooleanLiteral _,
                 Expression.StringLiteral _,
                 Expression.CharLiteral _,
                 Expression.SignedIntegerLiteral _,
                 Expression.UnsignedIntegerLiteral _,
                 Expression.FloatLiteral _,
                 Expression.InstanceFieldAccess _,
                 Expression.StaticFieldAccess _ -> Set.of(interpreter.evaluateExpression(expression, stack));
            case Expression.LeftUnary(_, _, Expression e) -> topVariables(e, stack);
            case Expression.Binary(_, Expression left, _, Expression right) -> {
                Collection<Variable> l = topVariables(left, stack);
                Collection<Variable> r = topVariables(right, stack);
                Set<Variable> result = new HashSet<>();
                result.addAll(l);
                result.addAll(r);
                yield result;
            }
            case Expression.New(_, Expression e) -> topVariables(e, stack);
            case Expression.TypeCast(_, _, Expression e) -> topVariables(e, stack);
            case Expression.InstanceMethodCall(_, Expression instance, _, List<Expression> arguments) -> {
                Set<Variable> variables = new HashSet<>(topVariables(instance, stack));
                for (Expression e : arguments) {
                    variables.addAll(topVariables(e, stack));
                }
                yield variables;
            }
            case Expression.ArrayCreation(_, Expression defaultValue, Expression length) -> {
                Collection<Variable> d = topVariables(defaultValue, stack);
                Collection<Variable> l = topVariables(length, stack);
                Set<Variable> result = new HashSet<>();
                result.addAll(d);
                result.addAll(l);
                yield result;
            }
            case Expression.ArrayLiteralCreation(_, List<Expression> values) -> {
                Set<Variable> variables = new HashSet<>();
                for (Expression e : values) {
                    variables.addAll(topVariables(e, stack));
                }
                yield variables;
            }
            case Expression.ConstructorCall _ -> throw new NotImplementedException();
            case Expression.StaticMethodCall _ -> throw new NotImplementedException();
        };
    }

    @Override
    public void run(@Nullable Variable ignored) {
        Variable result = interpreter.evaluateExpression(condition, stack);
        if (result instanceof PrimitiveVariable primitiveVariable
                && primitiveVariable.javaValue() instanceof Bool bool
                && bool.isValue()) {
            creationFrame.getInterruptQueue().add(new Interrupt(interruptMutexID, body, stack, imports));
        }
    }

    void destroy() {
        for (Variable v : topVariables) {
            v.stopListening(this);
        }
    }

}
