import org.jspecify.annotations.NullMarked;

@NullMarked
module de.til7701.fluxus.common {
    requires static lombok;
    requires org.slf4j;

    requires de.til7701.fluxus.ast;
    requires de.til7701.fluxus.parser;
    requires org.antlr.antlr4.runtime;
    requires org.jspecify;
    requires jdk.jshell;

    exports de.til7701.fluxus.common.environment;
    exports de.til7701.fluxus.common.flumen;
    exports de.til7701.fluxus.common;
    exports de.til7701.fluxus.common.fons;
    exports de.til7701.fluxus.common.util;
    exports de.til7701.fluxus.common.util.ints;
    exports de.til7701.fluxus.common.type;
    exports de.til7701.fluxus.common.type.klass;
    exports de.til7701.fluxus.common.type.trait;
}
