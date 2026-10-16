package de.til7701.javelin.common.util;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.SimpleType;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.common.shaft.Bool;
import de.til7701.javelin.common.shaft.I;
import de.til7701.javelin.common.shaft.Str;

import java.util.Map;

public class Java {

    private static final Map<Class<?>, Type> typeMappingCache = Map.of(
            String.class, Str.TYPE,
            Str.class, Str.TYPE,
            Class.forPrimitiveName("boolean"), Bool.TYPE,
            Class.forPrimitiveName("int"), I.TYPE,
            Boolean.class, Bool.TYPE,
            Bool.class, Bool.TYPE
    );

    public static Type mapTypes(Class<?> clazz) {
        return typeMappingCache.getOrDefault(clazz, new SimpleType(Span.undefined(), clazz.getSimpleName()));
    }

}
