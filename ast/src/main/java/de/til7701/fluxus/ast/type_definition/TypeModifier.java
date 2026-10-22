package de.til7701.fluxus.ast.type_definition;

import de.til7701.fluxus.ast.Node;
import de.til7701.fluxus.ast.Span;

public record TypeModifier(
        Span span,
        TypeModifierValue value
) implements Node {
}
