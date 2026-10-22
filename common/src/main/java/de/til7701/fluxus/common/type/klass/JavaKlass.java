package de.til7701.fluxus.common.type.klass;

import de.til7701.fluxus.ast.statement.Import;
import de.til7701.fluxus.ast.type.Type;
import de.til7701.fluxus.common.type.Metod;

import java.util.List;
import java.util.Map;

public record JavaKlass(
        boolean isPub,
        Class<?> javaClass,
        String fullyQualifiedFluxusName,
        List<Metod> methods,
        Map<String, List<Metod>> methodsGroupedByName,
        Type type
) implements Klass {

    public List<Import> imports() {
        return List.of();
    }

    @Override
    public String toString() {
        return javaClass.toString();
    }

}
