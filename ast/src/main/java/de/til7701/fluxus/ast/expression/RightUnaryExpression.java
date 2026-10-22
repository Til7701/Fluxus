package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.Span;

public record RightUnaryExpression(
        Span span,
        Expression expression,
        RightUnaryOperator operator
) implements Expression {
}
