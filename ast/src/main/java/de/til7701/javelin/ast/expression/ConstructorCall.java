package de.til7701.javelin.ast.expression;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.Type;

import java.util.List;

public record ConstructorCall(
        Span span,
        Type type,
        List<Expression> arguments
) implements Expression {
}
