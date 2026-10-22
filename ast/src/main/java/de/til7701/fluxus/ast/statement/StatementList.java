package de.til7701.fluxus.ast.statement;

import de.til7701.fluxus.ast.Span;

import java.util.List;

public record StatementList(
        Span span,
        List<Statement> statements
) implements Statement {
}
