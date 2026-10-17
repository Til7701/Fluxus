package de.til7701.javelin.ast.type;

import de.til7701.javelin.ast.Span;

import java.util.function.Function;

public record SimpleType(
        Span span,
        String name
) implements Type {

    @Override
    public Type mapNames(Function<String, String> nameMapper) {
        return new SimpleType(
                span,
                nameMapper.apply(name)
        );
    }

}
