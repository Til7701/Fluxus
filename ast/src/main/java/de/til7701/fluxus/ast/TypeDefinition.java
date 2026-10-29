package de.til7701.fluxus.ast;

import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

public sealed interface TypeDefinition extends Ast {

    record TypeModifier(Span span,
                        TypeModifierValue value
    ) implements Node {
    }

    enum TypeModifierValue {
    }

    record ClassDefinition(Span span,
                           @Nullable DocComment docComment,
                           List<Statement.Import> imports,
                           List<TypeModifier> modifiers,
                           List<ClassFieldDefinition> fields,
                           List<ConstructorDefinition> constructors,
                           List<MethodDefinition> methods
    ) implements TypeDefinition {
    }

    record ClassFieldDefinition(Span span, @Nullable DocComment docComment,
                                List<FieldModifier> modifiers, Type type, String name, Expression value
    ) implements Node {
    }

    record FieldModifier(Span span,
                         FieldModifierValue value
    ) implements Node {
    }

    enum FieldModifierValue {
        STATIC,
    }

    record ConstructorDefinition(Span span, @Nullable DocComment docComment,
                                 List<MethodModifier> modifiers, MethodParameters parameters, Statement body
    ) implements Node {
    }

    record MethodDefinition(Span span,
                            @Nullable DocComment docComment,
                            List<MethodModifier> modifiers,
                            @Nullable Type returnType,
                            String name,
                            MethodParameters parameters,
                            Statement body
    ) implements Node {
    }

    record MethodModifier(Span span,
                          MethodModifierValue value
    ) implements Node {
    }

    enum MethodModifierValue {
        STATIC,
    }

    record MethodParameters(Span span,
                            List<MethodParameter> parameters
    ) implements Node {
    }

    record MethodParameter(Span span,
                           Type type, String name
    ) implements Node {
    }

}
