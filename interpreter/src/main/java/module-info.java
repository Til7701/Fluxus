import org.jspecify.annotations.NullMarked;

@NullMarked
module de.til7701.fluxus.interpreter {
    requires de.til7701.fluxus.common;
    requires de.til7701.fluxus.ast;
    requires de.til7701.fluxus.n;

    requires static lombok;

    requires org.jspecify;
    requires org.slf4j;

    exports de.til7701.fluxus.interpreter;
}
