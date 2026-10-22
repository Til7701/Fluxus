package de.til7701.fluxus.ast;

import de.til7701.fluxus.ast.methods.MethodDefinition;
import de.til7701.fluxus.ast.methods.MethodModifier;
import de.til7701.fluxus.ast.methods.MethodParameter;
import de.til7701.fluxus.ast.methods.MethodParameters;
import de.til7701.fluxus.ast.statement.Statement;
import de.til7701.fluxus.ast.type.Type;
import de.til7701.fluxus.ast.type.TypeList;
import de.til7701.fluxus.ast.type_definition.TypeModifier;
import de.til7701.fluxus.ast.type_definition.classes.ClassFieldDefinition;
import de.til7701.fluxus.ast.type_definition.classes.ConstructorDefinition;
import de.til7701.fluxus.ast.type_definition.classes.FieldModifier;

public sealed interface Node permits Ast, MethodDefinition, MethodModifier, MethodParameter, MethodParameters, Statement, Type, TypeList, TypeModifier, ClassFieldDefinition, ConstructorDefinition, FieldModifier {

    Span span();

}
