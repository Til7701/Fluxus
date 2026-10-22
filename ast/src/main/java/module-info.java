import org.jspecify.annotations.NullMarked;

@NullMarked
module de.til7701.fluxus.ast {
    requires static lombok;
    requires org.jspecify;

    exports de.til7701.fluxus.ast;
    exports de.til7701.fluxus.ast.expression;
    exports de.til7701.fluxus.ast.statement;
    exports de.til7701.fluxus.ast.type;
    exports de.til7701.fluxus.ast.type.trait;
    exports de.til7701.fluxus.ast.type_definition;
    exports de.til7701.fluxus.ast.type_definition.classes;
    exports de.til7701.fluxus.ast.methods;
}
