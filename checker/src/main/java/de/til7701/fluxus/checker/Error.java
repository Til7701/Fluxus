package de.til7701.fluxus.checker;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.type.Type;
import de.til7701.fluxus.common.type.TypeDescriptor;

import java.util.Arrays;

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
        @Override
        public String toString() {
            return "MethodNotFound{" +
                    "span=" + span +
                    ", methodName='" + methodName + '\'' +
                    ", argumentTypes=" + Arrays.deepToString(argumentTypes) +
                    ", typeDescriptor=" + typeDescriptor +
                    '}';
        }
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
