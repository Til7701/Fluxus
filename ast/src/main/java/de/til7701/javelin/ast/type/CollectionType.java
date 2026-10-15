package de.til7701.javelin.ast.type;

import de.til7701.javelin.ast.Span;

import java.util.function.Function;

public record CollectionType(
        Span span,
        Type indexType,
        Type elementType
) implements Type {

    @Override
    public Type mapNames(Function<String, String> nameMapper) {
        return new CollectionType(
                span,
                indexType.mapNames(nameMapper),
                elementType.mapNames(nameMapper)
        );
    }

    @Override
    public boolean isAssignableTo(Type other) {
        return other instanceof CollectionType(_, Type otherIndexType, Type otherElementType);
    }

    @Override
    public String name() {
        return "";
    }

}
