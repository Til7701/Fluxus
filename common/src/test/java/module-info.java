module de.til7701.fluxus.test {
    requires de.til7701.fluxus.common;

    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;
    requires de.til7701.fluxus.ast;
    requires static lombok;

    opens de.til7701.fluxus.test.common.util.ints to org.junit.platform.commons;
    opens de.til7701.fluxus.test.common.flumen to org.junit.platform.commons;
}
