package de.til7701.fluxus.common.fons;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.fons.util.FluxusType;
import de.til7701.fluxus.common.fons.util.Primitive;
import de.til7701.fluxus.common.fons.util.Primitives;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.util.Ignore;
import de.til7701.fluxus.common.util.n.FN;
import de.til7701.fluxus.common.util.n.IN;
import de.til7701.fluxus.common.util.n.UN;
import lombok.Getter;

@FluxusType(
        fullyQualifiedFluxusName = de.til7701.fluxus.common.fons.U.FLUXUS_TYPE_NAME
)
public final class U implements Primitive {

    static final String FLUXUS_TYPE_NAME = "fluxus.fons.U";
    public static final Type.Simple TYPE = new Type.Simple(Span.undefined(), FLUXUS_TYPE_NAME);

    public static final LazyConstant<TypeDescriptor> DESCRIPTOR = LazyConstant.of(() -> Primitives.loadJavaClass(U.class, TYPE));

    @Getter(onMethod_ = {@Ignore})
    private final UN value;

    @Ignore
    private U(UN value) {
        this.value = value;
    }

    @Ignore
    public static U of(UN value) {
        return new U(value);
    }

    public static U add(U left, U right) {
        return of(UN.add(left.value, right.value));
    }

    public static U sub(U left, U right) {
        return of(UN.sub(left.value, right.value));
    }

    public static U mul(U left, U right) {
        return of(UN.mul(left.value, right.value));
    }

    public static U div(U left, U right) {
        return of(UN.div(left.value, right.value));
    }

    public static Bool eq(U left, U right) {
        return Bool.of(UN.eq(left.value, right.value));
    }

    public static Bool neq(U left, U right) {
        return Bool.of(UN.neq(left.value, right.value));
    }

    public static Bool gt(U left, U right) {
        return Bool.of(UN.gt(left.value, right.value));
    }

    public static Bool gte(U left, U right) {
        return Bool.of(UN.gte(left.value, right.value));
    }

    public static Bool lt(U left, U right) {
        return Bool.of(UN.lt(left.value, right.value));
    }

    public static Bool lte(U left, U right) {
        return Bool.of(UN.lte(left.value, right.value));
    }

    public static U bit_and(U left, U right) {
        return of(UN.bitAnd(left.value, right.value));
    }

    public static U bit_or(U left, U right) {
        return of(UN.bitOr(left.value, right.value));
    }

    public static U bit_xor(U left, U right) {
        return of(UN.bitXOr(left.value, right.value));
    }

    public static U shift_left(U left, U right) {
        return of(UN.shiftLeft(left.value, right.value));
    }

    public static U shift_right(U left, U right) {
        return of(UN.shiftRight(left.value, right.value));
    }

    public static String as__fluxus_flumen_Str(U value, PType ignored) {
        return value.value.toString();
    }

    public static I as__fluxus_fons_I(U value, PType target) {
        if (target.getValue() instanceof Type.Trait.I iType)
            return de.til7701.fluxus.common.fons.I.of(IN.of(value.getValue().getValue(), iType.bitCount()));
        else
            throw new RuntimeException();
    }

    public static U as__fluxus_fons_U(U value, PType target) {
        if (target.getValue() instanceof Type.Trait.U uType)
            return de.til7701.fluxus.common.fons.U.of(UN.of(value.getValue().getValue(), uType.bitCount()));
        else
            throw new RuntimeException();
    }

    public static F as__fluxus_fons_F(U value, PType target) {
        if (target.getValue() instanceof Type.Trait.F fType)
            return de.til7701.fluxus.common.fons.F.of(FN.of(value.getValue().getValue(), fType.bitCount()));
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
        return value + "U" + this.getValue().getBitCount();
    }

}
