package de.til7701.fluxus.ast.statement;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.expression.Expression;
import org.jspecify.annotations.Nullable;

public record ReturnStatement(
        Span span,
        @Nullable Expression value
) implements Statement {
}
