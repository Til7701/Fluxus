package de.til7701.fluxus.common.fons;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.fons.util.FluxusType;
import de.til7701.fluxus.common.fons.util.Primitive;
import de.til7701.fluxus.common.fons.util.Primitives;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.util.Ignore;
import lombok.Getter;

@FluxusType(
        fullyQualifiedFluxusName = PType.FLUXUS_TYPE_NAME
)
public final class PType implements Primitive {

    static final String FLUXUS_TYPE_NAME = "fluxus.fons.Type";
    public static final Type.Simple TYPE = new Type.Simple(Span.undefined(), FLUXUS_TYPE_NAME);

    public static final LazyConstant<TypeDescriptor> DESCRIPTOR = LazyConstant.of(() -> Primitives.loadJavaClass(PType.class, TYPE));

    @Getter(onMethod_ = {@Ignore})
    private final Type value;

    @Ignore
    private PType(Type value) {
        this.value = value;
    }

    @Ignore
    public static PType of(Type value) {
        return new PType(value);
    }

    @Ignore
    @Override
    public TypeDescriptor descriptor() {
        return DESCRIPTOR.get();
    }

}
