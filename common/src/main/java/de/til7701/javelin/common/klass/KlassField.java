package de.til7701.javelin.common.klass;

import de.til7701.javelin.ast.expression.Expression;
import de.til7701.javelin.ast.type.Type;

public record KlassField(
        String name,
        Type type,
        Expression init
) {
}
