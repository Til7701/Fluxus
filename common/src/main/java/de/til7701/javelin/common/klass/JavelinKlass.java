package de.til7701.javelin.common.klass;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.statement.Import;
import de.til7701.javelin.ast.type.IType;
import de.til7701.javelin.ast.type.SimpleType;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.ast.type.UType;
import de.til7701.javelin.common.environment.Environment;
import de.til7701.javelin.common.environment.Imports;
import de.til7701.javelin.common.shaft.I;
import de.til7701.javelin.common.shaft.U;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record JavelinKlass(
        List<Import> imports,
        boolean isPub,
        boolean isNative,
        String fullyQualifiedJavelinName,
        List<KlassField> fields,
        List<JavelinMetod> constructors,
        List<Metod> methods,
        Map<String, List<Metod>> methodsGroupedByName
) implements Klass {

    public Optional<JavelinMetod> getConstructor(Type[] argumentTypes, Environment environment) {
        Imports imports = environment.getPrimitiveImports();
        this.imports().forEach(imports::addImport);

        for (JavelinMetod metod : constructors) {
            Type[] parameterTypes = Arrays.stream(metod.parameterTypes())
                    .map(type -> type.mapNames(imports::map))
                    .map(type -> {
                        if (type instanceof IType)
                            return new SimpleType(Span.undefined(), I.TYPE.name());
                        if (type instanceof UType)
                            return new SimpleType(Span.undefined(), U.TYPE.name());
                        else
                            return type;
                    })
                    .toArray(Type[]::new);
            if (parameterTypes.length != argumentTypes.length) {
                continue;
            }
            boolean match = true;
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!argumentTypes[i].isAssignableTo(parameterTypes[i])) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return Optional.of(metod);
            }
        }

        return Optional.empty();
    }

}
