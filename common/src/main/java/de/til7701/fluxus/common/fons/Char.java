package de.til7701.fluxus.common.fons;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.type.SimpleType;
import de.til7701.fluxus.common.fons.util.FluxusType;
import de.til7701.fluxus.common.fons.util.Primitive;
import de.til7701.fluxus.common.fons.util.Primitives;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.util.Ignore;
import lombok.Getter;

@FluxusType(
        fullyQualifiedFluxusName = Char.FLUXUS_TYPE_NAME
)
public final class Char implements Primitive {

    static final String FLUXUS_TYPE_NAME = "fluxus.fons.Char";
    public static final SimpleType TYPE = new SimpleType(Span.undefined(), FLUXUS_TYPE_NAME);

    public static final LazyConstant<TypeDescriptor> DESCRIPTOR = LazyConstant.of(() -> Primitives.loadJavaClass(Char.class, TYPE));

    @Getter(onMethod_ = {@Ignore})
    private final char value;

    @Ignore
    private Char(char value) {
        this.value = value;
    }

    @Ignore
    public static Char of(char value) {
        return new Char(value);
    }

    public static Bool eq(Char left, Char right) {
        return Bool.of(left.value == right.value);
    }

    public static Bool neq(Char left, Char right) {
        return Bool.of(left.value != right.value);
    }

    @Ignore
    @Override
    public TypeDescriptor descriptor() {
        return DESCRIPTOR.get();
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
