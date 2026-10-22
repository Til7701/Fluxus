package de.til7701.fluxus.ast.type;

import de.til7701.fluxus.ast.Span;

import java.util.function.Function;

public record GenericType(
        Span span,
        Type baseType,
        TypeList typeArguments
) implements Type {

    @Override
    public Type mapNames(Function<String, String> nameMapper) {
        return new GenericType(
                span,
                baseType.mapNames(nameMapper),
                typeArguments.mapNames(nameMapper)
        );
    }

    @Override
    public String name() {
        return "";
    }

}
