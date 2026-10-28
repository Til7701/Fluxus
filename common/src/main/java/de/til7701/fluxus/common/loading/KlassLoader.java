package de.til7701.fluxus.common.loading;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Statement;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.ast.TypeDefinition;
import de.til7701.fluxus.common.environment.Imports;
import de.til7701.fluxus.common.fons.Void;
import de.til7701.fluxus.common.type.Metod;
import de.til7701.fluxus.common.type.klass.Field;
import de.til7701.fluxus.common.type.klass.FluxusKlass;
import de.til7701.fluxus.common.type.klass.FluxusMetod;
import de.til7701.fluxus.common.type.klass.Klass;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public final class KlassLoader {

    private final Map<String, TypeDefinition.TemplateDefinition> templates = new HashMap<>();
    private final TemplateInstantiator templateInstantiator = new TemplateInstantiator();

    public void addTemplate(String name, TypeDefinition.TemplateDefinition template) {
        templates.put(name, template);
    }

    public FluxusKlass instantiateTemplate(String klassName, TypeDefinition.TemplateInstance instance) {
        TypeDefinition.TemplateDefinition template = templates.get(instance.templateType().name());
        if (template == null) throw new RuntimeException("Template not found");
        return templateInstantiator.instantiate(klassName, template, instance);
    }

    public Klass loadKlass(String klassName, TypeDefinition.ClassDefinition classDefinition) {
        List<Statement.Import> imports = Stream.concat(
                        Stream.of(
                                new Statement.Import(Span.undefined(), klassName, null),
                                new Statement.Import(Span.undefined(), klassName, "This")
                        ),
                        classDefinition.imports().stream()
                )
                .toList();
        Imports importsMapper = new Imports(imports);
        final Type finalKlassType = new Type.Simple(classDefinition.span(), klassName);
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
                        constructorDefinition.span(),
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
                        methodDefinition.span(),
                        methodDefinition.modifiers().stream().anyMatch(m -> m.value() == TypeDefinition.MethodModifierValue.STATIC),
                        methodDefinition.name(),
                        methodDefinition.returnType() == null
                                ? Void.TYPE
                                : methodDefinition.returnType().mapNames(importsMapper::map),
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
