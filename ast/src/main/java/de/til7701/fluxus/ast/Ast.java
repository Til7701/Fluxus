package de.til7701.fluxus.ast;

public sealed interface Ast extends Node permits Script, TypeDefinition {
}
