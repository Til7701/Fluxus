package de.til7701.javelin.common.type.klass;

import de.til7701.javelin.ast.expression.Expression;
import de.til7701.javelin.ast.type.Type;

public record Field(
        String name,
        Type type,
        Expression init
) {
}
