package de.til7701.fluxus.common.type.klass;

import de.til7701.fluxus.ast.Statement;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.type.Metod;

import java.util.Arrays;

public record FluxusMetod(
        boolean isStatic,
        String name,
        Type returnType,
        Type[] parameterTypes,
        String[] parameterNames,
        Statement body
) implements Metod {

    @Override
    public String toString() {
        return "FluxusMetod{" +
                "isStatic=" + isStatic +
                ", name='" + name + '\'' +
                ", returnType=" + returnType +
                ", parameterTypes=" + Arrays.deepToString(parameterTypes) +
                ", parameterNames=" + Arrays.deepToString(parameterNames) +
                ", body=" + body +
                '}';
    }
}
