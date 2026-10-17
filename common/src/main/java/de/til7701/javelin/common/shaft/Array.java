package de.til7701.javelin.common.shaft;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.SimpleType;
import de.til7701.javelin.common.type.TypeDescriptor;
import de.til7701.javelin.common.type.trait.CollectionTrait;
import de.til7701.javelin.common.type.trait.UTrait;
import de.til7701.javelin.common.util.Ignore;

import java.util.Arrays;

@JavelinType(
        fullyQualifiedJavelinName = Array.JAVELIN_TYPE_NAME
)
public final class Array implements Primitive {

    static final String JAVELIN_TYPE_NAME = "javelin.shaft.Array";
    public static final SimpleType TYPE = new SimpleType(Span.undefined(), JAVELIN_TYPE_NAME);

    public static final LazyConstant<TypeDescriptor> DESCRIPTOR = LazyConstant.of(() -> Primitives.loadJavaClass(Array.class, TYPE));

    private final Object[] value;
    private final CollectionTrait descriptor;

    @Ignore
    private Array(Object[] value, TypeDescriptor elementType) {
        this.value = value;
        this.descriptor = new CollectionTrait(new UTrait(32), elementType);
    }

    @Ignore
    public static Array of(Object[] value, TypeDescriptor elementType) {
        return new Array(value, elementType);
    }

    public static Object index(Array array, U index) {
        int i = (int) index.getValue().getValue();
        return array.value[i];
    }

    public static Str as__javelin_shaft_Str(Array value) {
        return Str.of(Arrays.deepToString(value.value));
    }

    @Ignore
    @Override
    public TypeDescriptor descriptor() {
        return DESCRIPTOR.get();
    }

}
