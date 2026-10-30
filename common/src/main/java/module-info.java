import org.jspecify.annotations.NullMarked;

@NullMarked
module de.til7701.fluxus.common {
    requires de.til7701.fluxus.ast;
    requires de.til7701.fluxus.parser;
    requires de.til7701.fluxus.n;

    requires static lombok;

    requires org.jspecify;
    requires org.slf4j;

    exports de.til7701.fluxus.common.checker;
    exports de.til7701.fluxus.common.environment;
    exports de.til7701.fluxus.common.flumen;
    exports de.til7701.fluxus.common.folium;
    exports de.til7701.fluxus.common.fons.util;
    exports de.til7701.fluxus.common.fons;
    exports de.til7701.fluxus.common.type.klass;
    exports de.til7701.fluxus.common.type.trait;
    exports de.til7701.fluxus.common.type;
    exports de.til7701.fluxus.common.util;
    exports de.til7701.fluxus.common;
}
