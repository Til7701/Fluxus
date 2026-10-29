package de.til7701.fluxus.common.type.klass;

import de.til7701.fluxus.ast.DocComment;
import de.til7701.fluxus.ast.Statement;
import de.til7701.fluxus.common.environment.Environment;
import de.til7701.fluxus.common.environment.Imports;
import de.til7701.fluxus.common.type.Metod;
import de.til7701.fluxus.common.type.TypeDescriptor;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public sealed interface Klass extends TypeDescriptor permits JavaKlass, FluxusKlass {

    List<Statement.Import> imports();

    @Nullable DocComment docComment();

    String fullyQualifiedFluxusName();

    List<Metod> methods();

    Map<String, List<Metod>> methodsGroupedByName();

    default Optional<Metod> findMethod(String methodName, TypeDescriptor[] argumentTypes, Environment environment) {
        Imports imports = environment.getPrimitiveImports();
        this.imports().forEach(imports::addImport);

        List<Metod> metods = methodsGroupedByName().get(methodName);
        if (metods == null) {
            return Optional.empty();
        }

        for (Metod metod : metods) {
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
