package de.til7701.fluxus.common.fons;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.fons.util.*;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.util.Ignore;
import de.til7701.fluxus.n.FN;
import de.til7701.fluxus.n.IN;
import de.til7701.fluxus.n.UN;
import lombok.Getter;

@FluxusType(
        fullyQualifiedFluxusName = de.til7701.fluxus.common.fons.I.FLUXUS_TYPE_NAME
)
public final class I implements Primitive {

    static final String FLUXUS_TYPE_NAME = "fluxus.fons.I";
    public static final Type.Simple TYPE = new Type.Simple(Span.undefined(), FLUXUS_TYPE_NAME);

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

    public static I add(@FluxusArgName("left") I left, @FluxusArgName("right") I right) {
        return of(IN.add(left.value, right.value));
    }

    public static I sub(@FluxusArgName("left") I left, @FluxusArgName("right") I right) {
        return of(IN.sub(left.value, right.value));
    }

    public static I mul(@FluxusArgName("left") I left, @FluxusArgName("right") I right) {
        return of(IN.mul(left.value, right.value));
    }

    public static I div(@FluxusArgName("left") I left, @FluxusArgName("right") I right) {
        return of(IN.div(left.value, right.value));
    }

    public static Bool eq(@FluxusArgName("left") I left, @FluxusArgName("right") I right) {
        return Bool.of(IN.eq(left.value, right.value));
    }

    public static Bool neq(@FluxusArgName("left") I left, @FluxusArgName("right") I right) {
        return Bool.of(IN.neq(left.value, right.value));
    }

    public static Bool gt(@FluxusArgName("left") I left, @FluxusArgName("right") I right) {
        return Bool.of(IN.gt(left.value, right.value));
    }

    public static Bool gte(@FluxusArgName("left") I left, @FluxusArgName("right") I right) {
        return Bool.of(IN.gte(left.value, right.value));
    }

    public static Bool lt(@FluxusArgName("left") I left, @FluxusArgName("right") I right) {
        return Bool.of(IN.lt(left.value, right.value));
    }

    public static Bool lte(@FluxusArgName("left") I left, @FluxusArgName("right") I right) {
        return Bool.of(IN.lte(left.value, right.value));
    }

    public static I bit_and(@FluxusArgName("left") I left, @FluxusArgName("right") I right) {
        return of(IN.bitAnd(left.value, right.value));
    }

    public static I bit_or(@FluxusArgName("left") I left, @FluxusArgName("right") I right) {
        return of(IN.bitOr(left.value, right.value));
    }

    public static I bit_xor(@FluxusArgName("left") I left, @FluxusArgName("right") I right) {
        return of(IN.bitXOr(left.value, right.value));
    }

    public static I shift_left(@FluxusArgName("left") I left, @FluxusArgName("right") I right) {
        return of(IN.shiftLeft(left.value, right.value));
    }

    public static I shift_right(@FluxusArgName("left") I left, @FluxusArgName("right") I right) {
        return of(IN.shiftRight(left.value, right.value));
    }

    public static String as__fluxus_flumen_Str(@FluxusArgName("value") I value, @FluxusArgName("ignored") PType ignored) {
        return value.value.toString();
    }

    public static U as__fluxus_fons_U(@FluxusArgName("value") I value, @FluxusArgName("target") PType target) {
        if (IN.lt(value.getValue(), IN.of(0, value.value.getBitCount())))
            throw new CastNegativeToUnsignedException(value.getValue().toString());
        if (target.getValue() instanceof Type.Trait.U uType)
            return de.til7701.fluxus.common.fons.U.of(UN.of(value.getValue().getValue(), uType.bitCount()));
        else
            throw new RuntimeException();
    }

    public static I as__fluxus_fons_I(@FluxusArgName("value") I value, @FluxusArgName("target") PType target) {
        if (target.getValue() instanceof Type.Trait.I itype)
            return de.til7701.fluxus.common.fons.I.of(IN.of(value.getValue().getValue(), itype.bitCount()));
        else
            throw new RuntimeException();
    }

    public static F as__fluxus_fons_F(@FluxusArgName("value") I value, @FluxusArgName("target") PType target) {
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
        return value + "I" + this.getValue().getBitCount();
    }

}
