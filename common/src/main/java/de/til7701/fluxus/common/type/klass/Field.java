package de.til7701.fluxus.common.type.klass;

import de.til7701.fluxus.ast.expression.Expression;
import de.til7701.fluxus.ast.type.Type;

public record Field(
        boolean isStatic,
        String name,
        Type type,
        Expression init
) {
}
