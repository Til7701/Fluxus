package de.til7701.javelin.checker;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.common.klass.Klass;

public sealed interface Error permits Error.KlassNotFound, Error.MethodNotFound, Error.SymbolNotFound, Error.TypeMismatch {

    Span span();

    record KlassNotFound(
            Span span,
            Type klass
    ) implements Error {
    }

    record MethodNotFound(
            Span span,
            String methodName,
            Type[] argumentTypes,
            Klass klass
    ) implements Error {
    }

    record SymbolNotFound(
            Span span,
            String symbol
    ) implements Error {
    }

    record TypeMismatch(
            Span span,
            Type from,
            Type to
    ) implements Error {
    }

}
