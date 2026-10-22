package de.til7701.fluxus.ast.type_definition;

import de.til7701.fluxus.ast.Ast;
import de.til7701.fluxus.ast.type_definition.classes.ClassDefinition;

public sealed interface TypeDefinition extends Ast permits ClassDefinition {
}
