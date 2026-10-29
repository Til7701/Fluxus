package de.til7701.fluxus.common.type.klass;

import de.til7701.fluxus.ast.DocComment;
import de.til7701.fluxus.ast.Statement;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.type.Metod;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record JavaKlass(
        Class<?> javaClass,
        @Nullable DocComment docComment,
        String fullyQualifiedFluxusName,
        List<Metod> methods,
        Map<String, List<Metod>> methodsGroupedByName,
        Type type
) implements Klass {

    public List<Statement.Import> imports() {
        return List.of();
    }

    @Override
    public String toString() {
        return javaClass.toString();
    }

}
