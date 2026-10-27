package de.til7701.fluxus.common.type.trait;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.type.Metod;

import java.util.Arrays;

public record TraitMetod(
        boolean isStatic,
        String name,
        Type returnType,
        Type[] parameterTypes
) implements Metod {

    @Override
    public Span span() {
        return Span.undefined();
    }

    @Override
    public String toString() {
        return "TraitMetod{" +
                "isStatic=" + isStatic +
                ", name='" + name + '\'' +
                ", returnType=" + returnType +
                ", parameterTypes=" + Arrays.deepToString(parameterTypes) +
                '}';
    }

}
