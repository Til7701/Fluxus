package de.til7701.fluxus.ast.methods;

import de.til7701.fluxus.ast.Node;
import de.til7701.fluxus.ast.Span;

import java.util.List;

public record MethodParameters(
        Span span,
        List<MethodParameter> parameters
) implements Node {
}
