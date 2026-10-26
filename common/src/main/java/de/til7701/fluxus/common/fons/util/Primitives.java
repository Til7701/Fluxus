package de.til7701.fluxus.common.fons.util;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.fons.*;
import de.til7701.fluxus.common.fons.Void;
import de.til7701.fluxus.common.type.Metod;
import de.til7701.fluxus.common.type.klass.JavaKlass;
import de.til7701.fluxus.common.type.klass.JavaMetod;
import de.til7701.fluxus.common.util.Ignore;
import de.til7701.fluxus.common.util.Natives;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public final class Primitives {

    private static final Map<Class<?>, Type> typeMappingCache = Map.of(
            Class.forPrimitiveName("void"), de.til7701.fluxus.common.fons.Void.TYPE,
            Char.class, Char.TYPE,
            Bool.class, Bool.TYPE,
            I.class, I.TYPE,
            U.class, U.TYPE,
            F.class, F.TYPE,
            Array.class, Array.TYPE,
            Object.class, Void.TYPE,
            PType.class, PType.TYPE,
            String.class, new Type.Simple(Span.undefined(), "fluxus.flumen.Str")
    );

    public static JavaKlass loadJavaClass(Class<?> javaClass, Type type) {
        FluxusType fluxusType = javaClass.getAnnotation(FluxusType.class);
        String fullyQualifiedFluxusName = fluxusType.fullyQualifiedFluxusName();
        List<Metod> methods = loadJavaMethods(javaClass);
        Map<String, List<Metod>> methodsGroupedByName = methods.stream()
                .collect(Collectors.groupingBy(Metod::name));
        JavaKlass klass = new JavaKlass(javaClass, fullyQualifiedFluxusName, methods, methodsGroupedByName, type);
        log.debug("Loaded Klass: {}", klass);
        return klass;
    }

    private static List<Metod> loadJavaMethods(Class<?> javaClass) {
        List<Metod> metods = new ArrayList<>();

        Method[] javaMethods = javaClass.getDeclaredMethods();
        for (Method javaMethod : javaMethods) {
            if (((javaMethod.getModifiers() & Modifier.PUBLIC) == 0) || javaMethod.isAnnotationPresent(Ignore.class)) {
                continue;
            }

            String methodName = javaMethod.getName();
            Type returnType = mapTypes(javaMethod.getReturnType());
            Type[] parameterTypes = Arrays.stream(javaMethod.getParameterTypes())
                    .filter(c -> !c.equals(Natives.class))
                    .map(Primitives::mapTypes)
                    .toArray(Type[]::new);

            Metod metod = new JavaMetod(
                    (javaMethod.getModifiers() & Modifier.STATIC) != 0,
                    javaMethod,
                    methodName,
                    returnType,
                    parameterTypes,
                    Arrays.asList(javaMethod.getParameterTypes()).contains(Natives.class)
            );
            metods.add(metod);
        }
        return Collections.unmodifiableList(metods);
    }

    public static Type mapTypes(Class<?> clazz) {
        return Objects.requireNonNull(typeMappingCache.get(clazz), clazz.toString());
    }

}
