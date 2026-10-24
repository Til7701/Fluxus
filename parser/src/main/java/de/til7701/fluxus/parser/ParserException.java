package de.til7701.fluxus.parser;

import de.til7701.fluxus.ast.Span;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ParserException extends RuntimeException {

    private final Span span;

    public ParserException(Span span, String message) {
        super(message);
        this.span = span;
    }

}
