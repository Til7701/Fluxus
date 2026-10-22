import org.jspecify.annotations.NullMarked;

@NullMarked
module de.til7701.fluxus.checker {
    requires de.til7701.fluxus.ast;
    requires de.til7701.fluxus.common;
    requires org.jspecify;
    requires static lombok;

    exports de.til7701.fluxus.checker;
}
