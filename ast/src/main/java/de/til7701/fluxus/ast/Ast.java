package de.til7701.fluxus.ast;

import de.til7701.fluxus.ast.type_definition.TypeDefinition;

public sealed interface Ast extends Node permits Script, TypeDefinition {
}
