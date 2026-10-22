package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.Span;

import java.util.List;

public record InstanceMethodCall(
        Span span,
        Expression instance,
        String methodName,
        List<Expression> arguments
) implements Expression {
}
