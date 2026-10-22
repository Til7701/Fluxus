package de.til7701.fluxus.common.fons;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.type.SimpleType;
import de.til7701.fluxus.ast.type.trait.IType;
import de.til7701.fluxus.ast.type.trait.UType;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.util.Ignore;
import de.til7701.fluxus.common.util.ints.IN;
import de.til7701.fluxus.common.util.ints.UN;
import lombok.Getter;

@FluxusType(
        fullyQualifiedFluxusName = I.FLUXUS_TYPE_NAME
)
public final class I implements Primitive {

    static final String FLUXUS_TYPE_NAME = "fluxus.fons.I";
    public static final SimpleType TYPE = new SimpleType(Span.undefined(), FLUXUS_TYPE_NAME);

    public static final LazyConstant<TypeDescriptor> DESCRIPTOR = LazyConstant.of(() -> Primitives.loadJavaClass(I.class, TYPE));

    @Getter(onMethod_ = {@Ignore})
    private final IN value;

    @Ignore
    private I(IN value) {
        this.value = value;
    }

    @Ignore
    public static I of(IN value) {
        return new I(value);
    }

    public static I add(I left, I right) {
        return of(IN.add(left.value, right.value));
    }

    public static I sub(I left, I right) {
        return of(IN.sub(left.value, right.value));
    }

    public static I mul(I left, I right) {
        return of(IN.mul(left.value, right.value));
    }

    public static I div(I left, I right) {
        return of(IN.div(left.value, right.value));
    }

    public static Bool eq(I left, I right) {
        return Bool.of(IN.eq(left.value, right.value));
    }

    public static Bool neq(I left, I right) {
        return Bool.of(IN.neq(left.value, right.value));
    }

    public static Bool gt(I left, I right) {
        return Bool.of(IN.gt(left.value, right.value));
    }

    public static Bool gte(I left, I right) {
        return Bool.of(IN.gte(left.value, right.value));
    }

    public static Bool lt(I left, I right) {
        return Bool.of(IN.lt(left.value, right.value));
    }

    public static Bool lte(I left, I right) {
        return Bool.of(IN.lte(left.value, right.value));
    }

    public static I bit_and(I left, I right) {
        return of(IN.bitAnd(left.value, right.value));
    }

    public static I bit_or(I left, I right) {
        return of(IN.bitOr(left.value, right.value));
    }

    public static I bit_xor(I left, I right) {
        return of(IN.bitXOr(left.value, right.value));
    }

    public static I shift_left(I left, I right) {
        return of(IN.shiftLeft(left.value, right.value));
    }

    public static I shift_right(I left, I right) {
        return of(IN.shiftRight(left.value, right.value));
    }

    public static Str as__fluxus_fons_Str(I value, PType ignored) {
        return Str.of(value.value.toString());
    }

    public static U as__fluxus_fons_U(I value, PType target) {
        if (IN.lt(value.getValue(), IN.of(0, value.value.getBitCount())))
            throw new CastNegativeToUnsignedException(value.getValue().toString());
        if (target.getValue() instanceof UType uType)
            return U.of(UN.of(value.getValue().getValue(), uType.bitCount()));
        else
            throw new RuntimeException();
    }

    public static I as__fluxus_fons_I(I value, PType target) {
        if (target.getValue() instanceof IType itype)
            return I.of(IN.of(value.getValue().getValue(), itype.bitCount()));
        else
            throw new RuntimeException();
    }

    @Ignore
    @Override
    public TypeDescriptor descriptor() {
        return DESCRIPTOR.get();
    }

    @Ignore
    @Override
    public String toString() {
        return value + "I" + this.getValue().getBitCount();
    }

}
