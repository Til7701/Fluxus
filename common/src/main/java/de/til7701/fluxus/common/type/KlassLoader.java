package de.til7701.fluxus.common.type;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Statement;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.ast.TypeDefinition;
import de.til7701.fluxus.common.environment.Imports;
import de.til7701.fluxus.common.fons.Void;
import de.til7701.fluxus.common.type.klass.Field;
import de.til7701.fluxus.common.type.klass.FluxusKlass;
import de.til7701.fluxus.common.type.klass.FluxusMetod;
import de.til7701.fluxus.common.type.klass.Klass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public final class KlassLoader {

    public Klass loadKlassFromAst(String klassName, TypeDefinition typeDefinition) {
        return switch (typeDefinition) {
            case TypeDefinition.ClassDefinition classDefinition ->
                    loadKlassFromClassDefinition(klassName, classDefinition);
        };
    }

    private Klass loadKlassFromClassDefinition(String klassName, TypeDefinition.ClassDefinition classDefinition) {
        List<Statement.Import> imports = Stream.concat(
                        Stream.of(
                                new Statement.Import(Span.undefined(), klassName, null),
                                new Statement.Import(Span.undefined(), klassName, "This")
                        ),
                        classDefinition.imports().stream()
                )
                .toList();
        Imports importsMapper = new Imports(imports);
        Type klassType = new Type.Simple(classDefinition.span(), klassName);
        final Type finalKlassType = klassType;
        List<Field> fields = classDefinition.fields().stream()
                .map(fieldDefinition -> new Field(
                        fieldDefinition.modifiers().stream().anyMatch(f -> f.value() == TypeDefinition.FieldModifierValue.STATIC),
                        fieldDefinition.name(),
                        fieldDefinition.type().mapNames(importsMapper::map),
                        fieldDefinition.value()
                ))
                .toList();
        List<FluxusMetod> constructors = classDefinition.constructors().stream()
                .map(constructorDefinition -> new FluxusMetod(
                        true,
                        klassName,
                        finalKlassType,
                        constructorDefinition.parameters().parameters().stream()
                                .map(methodParameter -> methodParameter.type().mapNames(importsMapper::map))
                                .toArray(Type[]::new),
                        constructorDefinition.parameters().parameters().stream()
                                .map(TypeDefinition.MethodParameter::name)
                                .toArray(String[]::new),
                        constructorDefinition.body()
                ))
                .toList();
        List<Metod> methods = classDefinition.methods().stream()
                .map(methodDefinition -> (Metod) new FluxusMetod(
                        methodDefinition.modifiers().stream().anyMatch(m -> m.value() == TypeDefinition.MethodModifierValue.STATIC),
                        methodDefinition.name(),
                        methodDefinition.returnType().orElse(Void.TYPE).mapNames(importsMapper::map),
                        methodDefinition.parameters().parameters().stream()
                                .map(methodParameter -> methodParameter.type().mapNames(importsMapper::map))
                                .toArray(Type[]::new),
                        methodDefinition.parameters().parameters().stream()
                                .map(TypeDefinition.MethodParameter::name)
                                .toArray(String[]::new),
                        methodDefinition.body()
                ))
                .toList();
        return new FluxusKlass(
                imports,
                klassName,
                fields,
                constructors,
                methods,
                methods.stream().collect(Collectors.groupingBy(Metod::name)),
                finalKlassType
        );
    }

}
