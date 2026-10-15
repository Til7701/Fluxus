package de.til7701.javelin.common.klass;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.methods.MethodModifierValue;
import de.til7701.javelin.ast.methods.MethodParameter;
import de.til7701.javelin.ast.statement.Import;
import de.til7701.javelin.ast.type.GenericType;
import de.til7701.javelin.ast.type.SimpleType;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.ast.type_definition.TypeDefinition;
import de.til7701.javelin.ast.type_definition.TypeModifierValue;
import de.til7701.javelin.ast.type_definition.classes.ClassDefinition;
import de.til7701.javelin.common.shaft.JavelinType;
import de.til7701.javelin.common.util.Ignore;
import de.til7701.javelin.common.util.Java;
import de.til7701.javelin.common.util.Natives;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class KlassLoader {

    public Klass loadJavaClass(Class<?> javaClass) {
        JavelinType javelinType = javaClass.getAnnotation(JavelinType.class);
        String fullyQualifiedJavelinName = javelinType.fullyQualifiedJavelinName();
        List<Metod> methods = loadJavaMethods(javaClass);
        Map<String, List<Metod>> methodsGroupedByName = methods.stream()
                .collect(Collectors.groupingBy(Metod::name));
        Klass klass = new JavaKlass(true, javaClass, fullyQualifiedJavelinName, methods, methodsGroupedByName);
        log.debug("Loaded Klass: {}", klass);
        return klass;
    }

    private List<Metod> loadJavaMethods(Class<?> javaClass) {
        List<Metod> metods = new ArrayList<>();

        Method[] javaMethods = javaClass.getDeclaredMethods();
        for (Method javaMethod : javaMethods) {
            if (((javaMethod.getModifiers() & Modifier.PUBLIC) == 0) || javaMethod.isAnnotationPresent(Ignore.class)) {
                continue;
            }

            String methodName = javaMethod.getName();
            Type returnType = Java.mapTypes(javaMethod.getReturnType());
            Type[] parameterTypes = Arrays.stream(javaMethod.getParameterTypes())
                    .filter(c -> !c.equals(Natives.class))
                    .map(Java::mapTypes)
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

    public Klass loadKlassFromAst(String klassName, TypeDefinition typeDefinition) {
        return switch (typeDefinition) {
            case ClassDefinition classDefinition -> loadKlassFromClassDefinition(klassName, classDefinition);
            default -> throw new IllegalStateException("Unexpected value: " + typeDefinition);
        };
    }

    private Klass loadKlassFromClassDefinition(String klassName, ClassDefinition classDefinition) {
        Type klassType = new SimpleType(classDefinition.span(), klassName);
        if (!classDefinition.generics().types().isEmpty())
            klassType = new GenericType(classDefinition.span(), klassType, classDefinition.generics());
        final Type finalKlassType = klassType;
        List<Import> imports = Stream.concat(Stream.of(new Import(Span.undefined(), klassName, null)), classDefinition.imports().stream())
                .toList();
        List<KlassField> fields = classDefinition.fields().stream()
                .map(fieldDefinition -> new KlassField(
                        fieldDefinition.name(),
                        fieldDefinition.type(),
                        fieldDefinition.value()
                ))
                .toList();
        List<JavelinMetod> constructors = classDefinition.constructors().stream()
                .map(constructorDefinition -> new JavelinMetod(
                        true,
                        klassName,
                        finalKlassType,
                        constructorDefinition.parameters().parameters().stream()
                                .map(MethodParameter::type)
                                .toArray(Type[]::new),
                        constructorDefinition.parameters().parameters().stream()
                                .map(MethodParameter::name)
                                .toArray(String[]::new),
                        constructorDefinition.body()
                ))
                .toList();
        List<Metod> methods = classDefinition.methods().stream()
                .map(methodDefinition -> (Metod) new JavelinMetod(
                        methodDefinition.modifiers().stream().anyMatch(m -> m.value() == MethodModifierValue.STATIC),
                        methodDefinition.name(),
                        methodDefinition.returnType().orElse(new SimpleType(methodDefinition.span(), "None")),
                        methodDefinition.parameters().parameters().stream()
                                .map(MethodParameter::type)
                                .toArray(Type[]::new),
                        methodDefinition.parameters().parameters().stream()
                                .map(MethodParameter::name)
                                .toArray(String[]::new),
                        methodDefinition.body()
                ))
                .toList();
        return new JavelinKlass(
                imports,
                classDefinition.modifiers().stream().anyMatch(m -> m.value() == TypeModifierValue.PUB),
                classDefinition.modifiers().stream().anyMatch(m -> m.value() == TypeModifierValue.NATIVE),
                klassName,
                fields,
                constructors,
                methods,
                methods.stream().collect(Collectors.groupingBy(Metod::name))
        );
    }

}
