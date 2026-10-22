package de.til7701.fluxus.ast.type_definition.classes;

import de.til7701.fluxus.ast.Node;
import de.til7701.fluxus.ast.Span;

public record FieldModifier(
        Span span,
        FieldModifierValue value
) implements Node {
}
