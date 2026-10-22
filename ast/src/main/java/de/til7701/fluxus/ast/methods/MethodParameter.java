package de.til7701.fluxus.ast.methods;

import de.til7701.fluxus.ast.Node;
import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.type.Type;

public record MethodParameter(
        Span span,
        Type type,
        String name
) implements Node {
}
