package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.Span;

public record BooleanLiteralExpression(
        Span span,
        boolean value
) implements Expression {
}
