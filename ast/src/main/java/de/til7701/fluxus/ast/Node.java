package de.til7701.fluxus.ast;

public sealed interface Node permits Ast, Statement, Type, TypeDefinition.ClassFieldDefinition, TypeDefinition.ConstructorDefinition, TypeDefinition.FieldModifier, TypeDefinition.MethodDefinition, TypeDefinition.MethodModifier, TypeDefinition.MethodParameter, TypeDefinition.MethodParameters, TypeDefinition.TemplateVariable, TypeDefinition.TypeModifier, TypeList {

    Span span();

}
