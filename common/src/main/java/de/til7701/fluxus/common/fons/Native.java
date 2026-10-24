package de.til7701.fluxus.common.fons;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.type.SimpleType;
import de.til7701.fluxus.ast.type.Type;
import de.til7701.fluxus.common.fons.util.FluxusType;
import de.til7701.fluxus.common.fons.util.PanicException;
import de.til7701.fluxus.common.fons.util.Primitives;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.util.Ignore;
import de.til7701.fluxus.common.util.Natives;
import de.til7701.fluxus.common.util.n.UN;

@FluxusType(
        fullyQualifiedFluxusName = Native.FLUXUS_TYPE_NAME
)
public final class Native {

    static final String FLUXUS_TYPE_NAME = "fluxus.fons.Native";
    public static final Type TYPE = new SimpleType(Span.undefined(), FLUXUS_TYPE_NAME);

    public static final LazyConstant<TypeDescriptor> DESCRIPTOR = LazyConstant.of(() -> Primitives.loadJavaClass(Native.class, TYPE));

    @Ignore
    private Native() {
    }

    public static void panic() {
        throw new PanicException();
    }

    public static void printChar(Natives natives, Char c) {
        natives.stdOut().print(c.getValue());
    }

    public static U nanoTime() {
        long time = java.lang.System.nanoTime();
        return U.of(UN.of(time, 64));
    }

}
