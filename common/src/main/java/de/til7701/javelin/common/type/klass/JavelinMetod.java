package de.til7701.javelin.common.type.klass;

import de.til7701.javelin.ast.statement.Statement;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.common.type.Metod;

import java.util.Arrays;

public record JavelinMetod(
        boolean isStatic,
        String name,
        Type returnType,
        Type[] parameterTypes,
        String[] parameterNames,
        Statement body
) implements Metod {

    @Override
    public String toString() {
        return "JavelinMetod{" +
                "isStatic=" + isStatic +
                ", name='" + name + '\'' +
                ", returnType=" + returnType +
                ", parameterTypes=" + Arrays.deepToString(parameterTypes) +
                ", parameterNames=" + Arrays.deepToString(parameterNames) +
                ", body=" + body +
                '}';
    }
}
