package de.til7701.javelin.common.type.klass;

import de.til7701.javelin.ast.statement.Import;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.common.type.Metod;

import java.util.List;
import java.util.Map;

public record JavaKlass(
        boolean isPub,
        Class<?> javaClass,
        String fullyQualifiedJavelinName,
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
