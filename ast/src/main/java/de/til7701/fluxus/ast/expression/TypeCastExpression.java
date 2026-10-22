package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.type.Type;

public record TypeCastExpression(
        Span span,
        Type targetType,
        Expression expression
) implements Expression {
}
