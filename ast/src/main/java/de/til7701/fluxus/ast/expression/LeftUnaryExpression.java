package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.Span;

public record LeftUnaryExpression(
        Span span,
        LeftUnaryOperator operator,
        Expression expression
) implements Expression {
}
