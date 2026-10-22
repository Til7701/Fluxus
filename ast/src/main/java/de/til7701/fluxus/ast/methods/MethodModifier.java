package de.til7701.fluxus.ast.methods;

import de.til7701.fluxus.ast.Node;
import de.til7701.fluxus.ast.Span;

public record MethodModifier(
        Span span,
        MethodModifierValue value
) implements Node {
}
