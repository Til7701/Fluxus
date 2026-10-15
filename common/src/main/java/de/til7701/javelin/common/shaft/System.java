package de.til7701.javelin.common.shaft;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.SimpleType;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.common.util.Ignore;
import de.til7701.javelin.common.util.Natives;

@JavelinType(
        fullyQualifiedJavelinName = System.JAVELIN_TYPE_NAME
)
public final class System {

    static final String JAVELIN_TYPE_NAME = "javelin.shaft.System";
    public final Type TYPE = new SimpleType(Span.undefined(), JAVELIN_TYPE_NAME);

    @Ignore
    private System() {
    }

    public static void println(Natives natives, Str line) {
        natives.getStdOut().println(line.getValue());
    }

}
