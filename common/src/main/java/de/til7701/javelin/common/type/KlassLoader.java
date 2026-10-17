package de.til7701.javelin.common.type;

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
import de.til7701.javelin.common.environment.Imports;
import de.til7701.javelin.common.shaft.Void;
import de.til7701.javelin.common.type.klass.Field;
import de.til7701.javelin.common.type.klass.JavelinKlass;
import de.til7701.javelin.common.type.klass.JavelinMetod;
import de.til7701.javelin.common.type.klass.Klass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class KlassLoader {

    public Klass loadKlassFromAst(String klassName, TypeDefinition typeDefinition) {
        return switch (typeDefinition) {
            case ClassDefinition classDefinition -> loadKlassFromClassDefinition(klassName, classDefinition);
        };
    }

    private Klass loadKlassFromClassDefinition(String klassName, ClassDefinition classDefinition) {
        List<Import> imports = Stream.concat(Stream.of(new Import(Span.undefined(), klassName, null)), classDefinition.imports().stream())
                .toList();
        Imports importsMapper = new Imports(imports);
        Type klassType = new SimpleType(classDefinition.span(), klassName);
        if (!classDefinition.generics().types().isEmpty())
            klassType = new GenericType(classDefinition.span(), klassType, classDefinition.generics());
        final Type finalKlassType = klassType;
        List<Field> fields = classDefinition.fields().stream()
                .map(fieldDefinition -> new Field(
                        fieldDefinition.name(),
                        fieldDefinition.type().mapNames(importsMapper::map),
                        fieldDefinition.value()
                ))
                .toList();
        List<JavelinMetod> constructors = classDefinition.constructors().stream()
                .map(constructorDefinition -> new JavelinMetod(
                        true,
                        klassName,
                        finalKlassType,
                        constructorDefinition.parameters().parameters().stream()
                                .map(methodParameter -> methodParameter.type().mapNames(importsMapper::map))
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
                        methodDefinition.returnType().orElse(Void.TYPE).mapNames(importsMapper::map),
                        methodDefinition.parameters().parameters().stream()
                                .map(methodParameter -> methodParameter.type().mapNames(importsMapper::map))
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
                methods.stream().collect(Collectors.groupingBy(Metod::name)),
                finalKlassType
        );
    }

}
