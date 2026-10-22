package de.til7701.fluxus.ast.statement;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.expression.Expression;

public record VariableInitialization(
        Span span,
        String name,
        Expression value
) implements Statement {
}
