import org.jspecify.annotations.NullMarked;

@NullMarked
module de.til7701.javelin.ast {
    requires static lombok;
    requires org.jspecify;

    exports de.til7701.javelin.ast;
    exports de.til7701.javelin.ast.expression;
    exports de.til7701.javelin.ast.statement;
    exports de.til7701.javelin.ast.type;
    exports de.til7701.javelin.ast.type.trait;
    exports de.til7701.javelin.ast.type_definition;
    exports de.til7701.javelin.ast.type_definition.classes;
    exports de.til7701.javelin.ast.methods;
}
