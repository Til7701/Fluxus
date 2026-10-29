package de.til7701.fluxus.ast;

public record DocComment(
        Span span,
        String comment
) implements Node {
}
