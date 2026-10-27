package de.til7701.fluxus.common.type.klass;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.type.Metod;

import java.lang.reflect.Method;

public record JavaMetod(
        boolean isStatic,
        Method javaMethod,
        String name,
        Type returnType,
        Type[] parameterTypes,
        boolean needsNatives
) implements Metod {

    @Override
    public Span span() {
        return Span.undefined();
    }

}
