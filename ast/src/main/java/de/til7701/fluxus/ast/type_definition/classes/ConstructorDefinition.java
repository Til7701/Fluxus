package de.til7701.fluxus.ast.type_definition.classes;

import de.til7701.fluxus.ast.Node;
import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.methods.MethodModifier;
import de.til7701.fluxus.ast.methods.MethodParameters;
import de.til7701.fluxus.ast.statement.Statement;

import java.util.List;

public record ConstructorDefinition(
        Span span,
        List<MethodModifier> modifiers,
        MethodParameters parameters,
        Statement body
) implements Node {
}
