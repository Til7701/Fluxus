package de.til7701.fluxus.ast.type_definition.classes;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.methods.MethodDefinition;
import de.til7701.fluxus.ast.statement.Import;
import de.til7701.fluxus.ast.type.TypeList;
import de.til7701.fluxus.ast.type_definition.TypeDefinition;
import de.til7701.fluxus.ast.type_definition.TypeModifier;

import java.util.List;

public record ClassDefinition(
        Span span,
        List<Import> imports,
        List<TypeModifier> modifiers,
        List<ClassFieldDefinition> fields,
        List<ConstructorDefinition> constructors,
        List<MethodDefinition> methods
) implements TypeDefinition {
}
