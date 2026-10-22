package de.til7701.fluxus.common.fons;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.type.SimpleType;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.util.Ignore;
import de.til7701.fluxus.common.util.ints.UN;
import lombok.Getter;

@FluxusType(
        fullyQualifiedFluxusName = Str.FLUXUS_TYPE_NAME
)
public final class Str implements Primitive {

    static final String FLUXUS_TYPE_NAME = "fluxus.fons.Str";
    public static final SimpleType TYPE = new SimpleType(Span.undefined(), FLUXUS_TYPE_NAME);

    public static final LazyConstant<TypeDescriptor> DESCRIPTOR = LazyConstant.of(() -> Primitives.loadJavaClass(Str.class, TYPE));

    @Getter(onMethod_ = {@Ignore})
    private final String value;

    @Ignore
    private Str(String value) {
        this.value = value;
    }

    @Ignore
    public static Str of(String value) {
        return new Str(value);
    }

    public static Str add(Str left, Str right) {
        return of(left.value + right.value);
    }

    public static Bool eq(Str left, Str right) {
        return Bool.of(left.value.equals(right.value));
    }

    public static Str index(Str str, U i) {
        return Str.of(String.valueOf(str.value.charAt((int) i.getValue().getValue())));
    }

    public U length() {
        return U.of(UN.of(value.length(), 32));
    }

    @Ignore
    @Override
    public TypeDescriptor descriptor() {
        return DESCRIPTOR.get();
    }

}
