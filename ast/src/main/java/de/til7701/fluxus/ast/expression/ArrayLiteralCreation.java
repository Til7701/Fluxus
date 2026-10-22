package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.Span;

import java.util.List;

public record ArrayLiteralCreation(
        Span span,
        List<Expression> values
) implements Expression {
}
