package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.Span;

public record InstanceFieldAccess(
        Span span,
        Expression instance,
        String fieldName
) implements Expression {
}
