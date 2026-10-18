package de.til7701.javelin.common.shaft;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.SimpleType;
import de.til7701.javelin.common.type.TypeDescriptor;
import de.til7701.javelin.common.util.Ignore;
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

    public static U subtract(U left, U right) {
        return of(UN.sub(left.value, right.value));
    }

    public static U shift_left(U left, U right) {
        return of(UN.shift_left(left.value, right.value));
    }

    public static U shift_right(U left, U right) {
        return of(UN.shift_right(left.value, right.value));
    }

    public static Str as__javelin_shaft_Str(U value) {
        return Str.of(value.value.toString());
    }

    @Ignore
    @Override
    public TypeDescriptor descriptor() {
        return DESCRIPTOR.get();
    }
}
