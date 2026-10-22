package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.Span;

public record UnsignedIntegerLiteralExpression(
        Span span,
        long value,
        long bitCount
) implements Expression {
}
