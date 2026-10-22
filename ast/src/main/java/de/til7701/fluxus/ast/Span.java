package de.til7701.fluxus.ast;

public record Span(
        int startLine,
        int startCol,
        int endLine,
        int endCol
) {

    public static Span undefined() {
        return new Span(-1, -1, -1, -1);
    }

    @Override
    public String toString() {
        if (startLine == -1 && startCol == -1 && endLine == -1 && endCol == -1)
            return "Span.undefined";
        return "Span{" +
                startLine +
                ", " + startCol +
                ", " + endLine +
                ", " + endCol +
                '}';
    }
}
