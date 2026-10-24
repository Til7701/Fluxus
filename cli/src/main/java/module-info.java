import de.til7701.fluxus.cli.log.LoggerServiceProvider;
import org.jspecify.annotations.NullMarked;

@NullMarked
module de.til7701.fluxus.cli {
    requires de.til7701.fluxus.ast;
    requires de.til7701.fluxus.common;
    requires de.til7701.fluxus.parser;
    requires de.til7701.fluxus.interpreter;

    requires info.picocli;
    requires org.slf4j;
    requires org.jspecify;
    requires static lombok;

    opens de.til7701.fluxus.cli.commands to info.picocli;
    opens de.til7701.fluxus.cli.mixins to info.picocli;

    provides org.slf4j.spi.SLF4JServiceProvider with LoggerServiceProvider;

    exports de.til7701.fluxus.cli;

    // For tests. Find a way to avoid it in the future.
    exports de.til7701.fluxus.cli.commands to de.til7701.fluxus.test;
}
