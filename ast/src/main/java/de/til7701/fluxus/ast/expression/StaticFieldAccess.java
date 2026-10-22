package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.type.Type;

public record StaticFieldAccess(
        Span span,
        Type type,
        String fieldName
) implements Expression {
}
