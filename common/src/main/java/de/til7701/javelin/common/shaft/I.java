package de.til7701.javelin.common.shaft;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.SimpleType;
import de.til7701.javelin.common.type.TypeDescriptor;
import de.til7701.javelin.common.util.Ignore;
import de.til7701.javelin.common.util.ints.IN;
import lombok.Getter;

@JavelinType(
        fullyQualifiedJavelinName = I.JAVELIN_TYPE_NAME
)
public final class I implements Primitive {

    static final String JAVELIN_TYPE_NAME = "javelin.shaft.I";
    public static final SimpleType TYPE = new SimpleType(Span.undefined(), JAVELIN_TYPE_NAME);

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

    public static I subtract(I left, I right) {
        return of(IN.sub(left.value, right.value));
    }

    public static Str as__javelin_shaft_Str(I value) {
        return Str.of(value.value.toString());
    }

    @Ignore
    @Override
    public TypeDescriptor descriptor() {
        return DESCRIPTOR.get();
    }
}
