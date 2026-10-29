package de.til7701.fluxus.common.type.klass;

import de.til7701.fluxus.ast.DocComment;
import de.til7701.fluxus.ast.Expression;
import de.til7701.fluxus.ast.Type;
import org.jspecify.annotations.Nullable;

public record Field(
        @Nullable DocComment docComment,
        boolean isStatic,
        String name,
        Type type,
        Expression init
) {
}
