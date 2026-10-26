package de.til7701.fluxus.ast;

import java.util.List;

public record Script(
        Span span,
        List<Statement> statements
) implements Ast {
}
