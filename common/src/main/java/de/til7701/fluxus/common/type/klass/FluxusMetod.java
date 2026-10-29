package de.til7701.fluxus.common.type.klass;

import de.til7701.fluxus.ast.DocComment;
import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Statement;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.type.Metod;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

public record FluxusMetod(
        Span span,
        @Nullable DocComment docComment,
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
