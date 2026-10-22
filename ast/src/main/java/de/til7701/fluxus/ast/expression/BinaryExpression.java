package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.Span;

public record BinaryExpression(
        Span span,
        Expression left,
        BinaryOperator operator,
        Expression right
) implements Expression {
}
