package de.til7701.fluxus.common.fons;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.fons.util.FluxusType;
import de.til7701.fluxus.common.fons.util.Primitive;
import de.til7701.fluxus.common.fons.util.Primitives;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.util.Ignore;

@FluxusType(
        fullyQualifiedFluxusName = Void.FLUXUS_TYPE_NAME
)
public final class Void implements Primitive {

    static final String FLUXUS_TYPE_NAME = "fluxus.fons.Void";
    public static final Type.Simple TYPE = new Type.Simple(Span.undefined(), FLUXUS_TYPE_NAME);

    public static final LazyConstant<TypeDescriptor> DESCRIPTOR = LazyConstant.of(() -> Primitives.loadJavaClass(Void.class, TYPE));

    private static final Void INSTANCE = new Void();

    @Ignore
    private Void() {
    }

    @Ignore
    public static Void of() {
        return INSTANCE;
    }

    @Ignore
    @Override
    public TypeDescriptor descriptor() {
        return DESCRIPTOR.get();
    }

}
