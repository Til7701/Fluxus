package de.til7701.javelin.common.shaft;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.SimpleType;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.common.type.TypeDescriptor;
import de.til7701.javelin.common.util.Ignore;
import lombok.Getter;

@JavelinType(
        fullyQualifiedJavelinName = PType.JAVELIN_TYPE_NAME
)
public final class PType implements Primitive {

    static final String JAVELIN_TYPE_NAME = "javelin.shaft.Type";
    public static final SimpleType TYPE = new SimpleType(Span.undefined(), JAVELIN_TYPE_NAME);

    public static final LazyConstant<TypeDescriptor> DESCRIPTOR = LazyConstant.of(() -> Primitives.loadJavaClass(PType.class, TYPE));

    @Getter(onMethod_ = {@Ignore})
    private final Type value;

    @Ignore
    private PType(Type value) {
        this.value = value;
    }

    @Ignore
    public static PType of(Type value) {
        return new PType(value);
    }

    @Ignore
    @Override
    public TypeDescriptor descriptor() {
        return DESCRIPTOR.get();
    }

}
