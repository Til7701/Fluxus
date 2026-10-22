package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.type.Type;

import java.util.List;

public record StaticMethodCall(
        Span span,
        Type type,
        String methodName,
        List<Expression> arguments
) implements Expression {
}
