package de.til7701.javelin.common.shaft;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.SimpleType;
import de.til7701.javelin.common.type.TypeDescriptor;
import de.til7701.javelin.common.util.Ignore;
import lombok.Getter;

@JavelinType(
        fullyQualifiedJavelinName = Str.JAVELIN_TYPE_NAME
)
public final class Str implements Primitive {

    static final String JAVELIN_TYPE_NAME = "javelin.shaft.Str";
    public static final SimpleType TYPE = new SimpleType(Span.undefined(), JAVELIN_TYPE_NAME);

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

    @Ignore
    @Override
    public TypeDescriptor descriptor() {
        return DESCRIPTOR.get();
    }

}
