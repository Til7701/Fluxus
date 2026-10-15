package de.til7701.javelin.checker;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.common.klass.Klass;

public record MethodNotFound(
        Span span,
        String methodName,
        Type[] argumentTypes,
        Klass klass
) {
}
