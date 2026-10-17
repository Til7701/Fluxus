package de.til7701.javelin.common.shaft;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.SimpleType;
import de.til7701.javelin.common.type.TypeDescriptor;
import de.til7701.javelin.common.util.Ignore;
import lombok.Getter;

@JavelinType(
        fullyQualifiedJavelinName = Bool.JAVELIN_TYPE_NAME
)
public final class Bool implements Primitive {

    static final String JAVELIN_TYPE_NAME = "javelin.shaft.Bool";
    public static final SimpleType TYPE = new SimpleType(Span.undefined(), JAVELIN_TYPE_NAME);

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

    public static Str as__javelin_shaft_Str(Bool value) {
        return Str.of(Boolean.toString(value.value));
    }

    @Ignore
    @Override
    public TypeDescriptor descriptor() {
        return DESCRIPTOR.get();
    }

}
