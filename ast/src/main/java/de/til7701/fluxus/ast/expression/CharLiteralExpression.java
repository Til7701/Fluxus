package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.Span;

public record CharLiteralExpression(
        Span span,
        String value
) implements Expression {
}
