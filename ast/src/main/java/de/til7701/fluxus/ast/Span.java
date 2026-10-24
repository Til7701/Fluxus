package de.til7701.fluxus.ast;

import org.jspecify.annotations.Nullable;

public record Span(
        @Nullable String filename,
        int startLine,
        int startCol,
        int endLine,
        int endCol
) {

    public static Span undefined() {
        return new Span(null, -1, -1, -1, -1);
    }

    @Override
    public String toString() {
        if (filename == null && startLine == -1 && startCol == -1 && endLine == -1 && endCol == -1)
            return "Span.undefined";
        return "Span{" +
                filename +
                ", " + startLine +
                ", " + startCol +
                ", " + endLine +
                ", " + endCol +
                '}';
    }
}
