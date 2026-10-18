package de.til7701.javelin.common.shaft;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.SimpleType;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.common.type.TypeDescriptor;
import de.til7701.javelin.common.util.Ignore;
import de.til7701.javelin.common.util.Natives;
import de.til7701.javelin.common.util.ints.UN;

@JavelinType(
        fullyQualifiedJavelinName = System.JAVELIN_TYPE_NAME
)
public final class System {

    static final String JAVELIN_TYPE_NAME = "javelin.shaft.System";
    public static final Type TYPE = new SimpleType(Span.undefined(), JAVELIN_TYPE_NAME);

    public static final LazyConstant<TypeDescriptor> DESCRIPTOR = LazyConstant.of(() -> Primitives.loadJavaClass(System.class, TYPE));

    @Ignore
    private System() {
    }

    public static void println(Natives natives, Str line) {
        natives.getStdOut().println(line.getValue());
    }

    public static U nanoTime() {
        long time = java.lang.System.nanoTime();
        return U.of(UN.of(time, 64));
    }

}
