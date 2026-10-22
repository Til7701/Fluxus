package de.til7701.fluxus.interpreter;

import de.til7701.fluxus.ast.Span;
import lombok.Getter;

@Getter
public sealed abstract class InterpreterException extends RuntimeException {

    protected final Span span;
    protected final Stack stack;
    private final LiteralStore literalStore;

    protected InterpreterException(Span span, Stack stack, LiteralStore literalStore, String message) {
        this.span = span;
        this.stack = stack;
        this.literalStore = literalStore;
        super(message);
    }

    public static final class CollectionAccessOutOfBounds extends InterpreterException {
        public CollectionAccessOutOfBounds(Span span, Stack stack, LiteralStore literalStore, String message) {
            super(span, stack, literalStore, message);
        }
    }

    public static final class MethodNotFound extends InterpreterException {
        public MethodNotFound(Span span, Stack stack, LiteralStore literalStore, String message) {
            super(span, stack, literalStore, message);
        }
    }

    public static final class CastNegativeToUnsigned extends InterpreterException {
        public CastNegativeToUnsigned(Span span, Stack stack, LiteralStore literalStore, String message) {
            super(span, stack, literalStore, message);
        }
    }

}
