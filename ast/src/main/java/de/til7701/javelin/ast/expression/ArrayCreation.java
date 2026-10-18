package de.til7701.javelin.ast.expression;

import de.til7701.javelin.ast.Span;

public record ArrayCreation(
        Span span,
        Expression defaultValue,
        Expression length
) implements Expression {
}
