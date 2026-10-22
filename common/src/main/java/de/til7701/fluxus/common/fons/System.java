package de.til7701.fluxus.common.fons;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.type.SimpleType;
import de.til7701.fluxus.ast.type.Type;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.util.Ignore;
import de.til7701.fluxus.common.util.Natives;
import de.til7701.fluxus.common.util.ints.UN;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@FluxusType(
        fullyQualifiedFluxusName = System.FLUXUS_TYPE_NAME
)
public final class System {

    static final String FLUXUS_TYPE_NAME = "fluxus.fons.System";
    public static final Type TYPE = new SimpleType(Span.undefined(), FLUXUS_TYPE_NAME);

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

    public static Str readFile(Str path) {
        try {
            return Str.of(Files.readString(Path.of(path.getValue())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
