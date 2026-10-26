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
        fullyQualifiedFluxusName = Bool.FLUXUS_TYPE_NAME
)
public final class Bool implements Primitive {

    static final String FLUXUS_TYPE_NAME = "fluxus.fons.Bool";
    public static final Type.Simple TYPE = new Type.Simple(Span.undefined(), FLUXUS_TYPE_NAME);

    public static final LazyConstant<TypeDescriptor> DESCRIPTOR = LazyConstant.of(() -> Primitives.loadJavaClass(Bool.class, TYPE));

    @Getter(onMethod_ = {@Ignore})
    private final boolean value;

    @Ignore
    private Bool(boolean value) {
        this.value = value;
    }

    @Ignore
    public static Bool of(boolean value) {
        return new Bool(value);
    }

    public static Bool and(Bool left, Bool right) {
        return of(left.value && right.value);
    }

    public static Bool eq(Bool left, Bool right) {
        return of(left.value == right.value);
    }

    public static Bool not(Bool value) {
        return of(!value.value);
    }

    public static String as__fluxus_flumen_Str(Bool value, PType ignored) {
        return Boolean.toString(value.value);
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
