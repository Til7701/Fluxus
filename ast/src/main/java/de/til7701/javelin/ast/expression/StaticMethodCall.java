package de.til7701.javelin.ast.expression;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.Type;

import java.util.List;

public record StaticMethodCall(
        Span span,
        Type type,
        String methodName,
        List<Expression> arguments
) implements Expression {
}
