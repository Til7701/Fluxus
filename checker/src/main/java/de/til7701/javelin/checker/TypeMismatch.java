package de.til7701.javelin.checker;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.Type;

public record TypeMismatch(
        Span span,
        Type from,
        Type to
) {
}
