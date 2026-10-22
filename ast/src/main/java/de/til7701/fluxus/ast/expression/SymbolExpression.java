package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.Span;

public record SymbolExpression(
        Span span,
        String identifier
) implements Expression {
}
