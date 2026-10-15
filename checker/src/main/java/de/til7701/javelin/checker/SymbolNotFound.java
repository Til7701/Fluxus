package de.til7701.javelin.checker;

import de.til7701.javelin.ast.Span;

public record SymbolNotFound(
        Span span,
        String symbol
) {
}
