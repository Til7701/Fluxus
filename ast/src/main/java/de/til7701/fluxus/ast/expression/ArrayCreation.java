package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.Span;

public record ArrayCreation(
        Span span,
        Expression defaultValue,
        Expression length
) implements Expression {
}
