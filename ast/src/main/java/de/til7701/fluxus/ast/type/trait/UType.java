package de.til7701.fluxus.ast.type.trait;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.type.Type;

import java.util.function.Function;

public record UType(
        Span span,
        long bitCount
) implements TraitType {

    @Override
    public Type mapNames(Function<String, String> nameMapper) {
        return this;
    }

    @Override
    public String name() {
        return "fluxus.fons.U";
    }

}
