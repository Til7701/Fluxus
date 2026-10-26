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
        fullyQualifiedFluxusName = de.til7701.fluxus.common.fons.F.FLUXUS_TYPE_NAME
)
public final class F implements Primitive {

    static final String FLUXUS_TYPE_NAME = "fluxus.fons.F";
    public static final Type.Simple TYPE = new Type.Simple(Span.undefined(), FLUXUS_TYPE_NAME);

    public static final LazyConstant<TypeDescriptor> DESCRIPTOR = LazyConstant.of(() -> Primitives.loadJavaClass(F.class, TYPE));

    @Getter(onMethod_ = {@Ignore})
    private final FN value;

    @Ignore
    private F(FN value) {
        this.value = value;
    }

    @Ignore
    public static F of(FN value) {
        return new F(value);
    }

    public static F add(F left, F right) {
        return of(FN.add(left.value, right.value));
    }

    public static F sub(F left, F right) {
        return of(FN.sub(left.value, right.value));
    }

    public static F mul(F left, F right) {
        return of(FN.mul(left.value, right.value));
    }

    public static F div(F left, F right) {
        return of(FN.div(left.value, right.value));
    }

    public static Bool eq(F left, F right) {
        return Bool.of(FN.eq(left.value, right.value));
    }

    public static Bool neq(F left, F right) {
        return Bool.of(FN.neq(left.value, right.value));
    }

    public static Bool gt(F left, F right) {
        return Bool.of(FN.gt(left.value, right.value));
    }

    public static Bool gte(F left, F right) {
        return Bool.of(FN.gte(left.value, right.value));
    }

    public static Bool lt(F left, F right) {
        return Bool.of(FN.lt(left.value, right.value));
    }

    public static Bool lte(F left, F right) {
        return Bool.of(FN.lte(left.value, right.value));
    }

    public static String as__fluxus_flumen_Str(F value, PType ignored) {
        return value.value.toString();
    }

    public static U as__fluxus_fons_U(F value, PType target) {
        if (target.getValue() instanceof Type.Trait.U utype)
            return de.til7701.fluxus.common.fons.U.of(UN.of((long) value.getValue().getValue(), utype.bitCount()));
        else
            throw new RuntimeException();
    }

    public static I as__fluxus_fons_I(F value, PType target) {
        if (target.getValue() instanceof Type.Trait.I itype)
            return de.til7701.fluxus.common.fons.I.of(IN.of((long) value.getValue().getValue(), itype.bitCount()));
        else
            throw new RuntimeException();
    }

    public static F as__fluxus_fons_F(F value, PType target) {
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
