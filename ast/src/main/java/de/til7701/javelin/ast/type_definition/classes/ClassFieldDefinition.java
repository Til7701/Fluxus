package de.til7701.javelin.ast.type_definition.classes;

import de.til7701.javelin.ast.Node;
import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.expression.Expression;
import de.til7701.javelin.ast.type.Type;

import java.util.List;
import java.util.Optional;

public record ClassFieldDefinition(
        Span span,
        List<FieldModifier> modifiers,
        Type type,
        String name,
        Expression value
) implements Node {
}
