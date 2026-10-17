package de.til7701.javelin.ast.type.trait;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.Type;

import java.util.function.Function;

public record CollectionType(
        Span span,
        Type indexType,
        Type elementType
) implements TraitType {

    @Override
    public Type mapNames(Function<String, String> nameMapper) {
        return new CollectionType(
                span,
                indexType.mapNames(nameMapper),
                elementType.mapNames(nameMapper)
        );
    }

    @Override
    public String name() {
        return elementType.name() + "[" + indexType.name() + "]";
    }

}
