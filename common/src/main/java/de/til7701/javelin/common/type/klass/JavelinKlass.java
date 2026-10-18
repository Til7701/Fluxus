package de.til7701.javelin.common.type.klass;

import de.til7701.javelin.ast.statement.Import;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.common.environment.Environment;
import de.til7701.javelin.common.environment.Imports;
import de.til7701.javelin.common.type.Metod;
import de.til7701.javelin.common.type.TypeDescriptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record JavelinKlass(
        List<Import> imports,
        boolean isPub,
        boolean isNative,
        String fullyQualifiedJavelinName,
        List<Field> fields,
        List<JavelinMetod> constructors,
        List<Metod> methods,
        Map<String, List<Metod>> methodsGroupedByName,
        Type type
) implements Klass {

    public Optional<JavelinMetod> getConstructor(TypeDescriptor[] argumentTypes, Environment environment) {
        Imports imports = environment.getPrimitiveImports();
        this.imports().forEach(imports::addImport);

        for (JavelinMetod metod : constructors) {
            TypeDescriptor[] parameterTypes = Arrays.stream(metod.parameterTypes())
                    .map(type -> type.mapNames(imports::map))
                    .map(type -> environment.getKlassRegister().find(type, imports))
                    .map(Optional::get)
                    .toArray(TypeDescriptor[]::new);
            if (parameterTypes.length != argumentTypes.length) {
                continue;
            }
            boolean match = true;
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!environment.isAssignable(argumentTypes[i], parameterTypes[i])) {
                    System.out.println(argumentTypes[i] + " " + parameterTypes[i]);
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

    @Override
    public String toString() {
        return fullyQualifiedJavelinName;
    }
}
