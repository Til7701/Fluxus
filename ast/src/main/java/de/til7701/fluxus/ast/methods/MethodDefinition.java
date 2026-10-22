package de.til7701.fluxus.ast.methods;

import de.til7701.fluxus.ast.Node;
import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.statement.Statement;
import de.til7701.fluxus.ast.type.Type;

import java.util.List;
import java.util.Optional;

public record MethodDefinition(
        Span span,
        List<MethodModifier> modifiers,
        Optional<Type> returnType,
        String name,
        MethodParameters parameters,
        Statement body
) implements Node {
}
