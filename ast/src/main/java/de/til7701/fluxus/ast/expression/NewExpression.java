package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.Span;

public record NewExpression(
        Span span,
        Expression expression
) implements Expression {
}
