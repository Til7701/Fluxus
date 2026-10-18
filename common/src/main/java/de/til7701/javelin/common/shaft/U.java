package de.til7701.javelin.common.shaft;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.SimpleType;
import de.til7701.javelin.ast.type.trait.IType;
import de.til7701.javelin.ast.type.trait.UType;
import de.til7701.javelin.common.type.TypeDescriptor;
import de.til7701.javelin.common.util.Ignore;
import de.til7701.javelin.common.util.ints.IN;
import de.til7701.javelin.common.util.ints.UN;
import lombok.Getter;

@JavelinType(
        fullyQualifiedJavelinName = U.JAVELIN_TYPE_NAME
)
public final class U implements Primitive {

    static final String JAVELIN_TYPE_NAME = "javelin.shaft.U";
    public static final SimpleType TYPE = new SimpleType(Span.undefined(), JAVELIN_TYPE_NAME);

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

    public static Str as__javelin_shaft_Str(U value, PType ignored) {
        return Str.of(value.value.toString());
    }

    public static I as__javelin_shaft_I(U value, PType target) {
        if (target.getValue() instanceof IType iType)
            return I.of(IN.of(value.getValue().getValue(), iType.bitCount()));
        else
            throw new RuntimeException();
    }

    public static U as__javelin_shaft_U(U value, PType target) {
        if (target.getValue() instanceof UType uType)
            return U.of(UN.of(value.getValue().getValue(), uType.bitCount()));
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
