package de.til7701.fluxus.ast.type_definition.classes;

import de.til7701.fluxus.ast.Node;
import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.expression.Expression;
import de.til7701.fluxus.ast.type.Type;

import java.util.List;

public record ClassFieldDefinition(
        Span span,
        List<FieldModifier> modifiers,
        Type type,
        String name,
        Expression value
) implements Node {
}
