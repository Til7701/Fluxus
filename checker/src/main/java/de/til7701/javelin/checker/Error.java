package de.til7701.javelin.checker;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.common.type.TypeDescriptor;

public sealed interface Error {

    Span span();

    record KlassNotFound(
            Span span,
            Type klass
    ) implements Error {
    }

    record MethodNotFound(
            Span span,
            String methodName,
            TypeDescriptor[] argumentTypes,
            TypeDescriptor typeDescriptor
    ) implements Error {
    }

    record SymbolNotFound(
            Span span,
            String symbol
    ) implements Error {
    }

    record TypeMismatch(
            Span span,
            TypeDescriptor from,
            TypeDescriptor to
    ) implements Error {
    }

    record Other(
            Span span,
            String message
    ) implements Error {
    }

}
