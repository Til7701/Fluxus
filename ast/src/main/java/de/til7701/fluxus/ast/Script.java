package de.til7701.fluxus.ast;

import de.til7701.fluxus.ast.statement.Statement;

import java.util.List;

public record Script(
        Span span,
        List<Statement> statements
) implements Ast {
}
