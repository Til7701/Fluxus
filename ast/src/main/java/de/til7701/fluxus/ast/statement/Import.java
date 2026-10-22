package de.til7701.fluxus.ast.statement;

import de.til7701.fluxus.ast.Span;
import org.jspecify.annotations.Nullable;

public record Import(
        Span span,
        String typeToImport,
        @Nullable String asName
) implements Statement {
}
