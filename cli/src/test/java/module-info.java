module de.til7701.fluxus.test {
    requires de.til7701.fluxus.cli;

    requires info.picocli;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;

    opens de.til7701.fluxus.test.cli to org.junit.platform.commons;
}
