package de.til7701.javelin.common.shaft;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.CollectionType;
import de.til7701.javelin.ast.type.SimpleType;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.common.util.Ignore;

import java.util.Arrays;

@JavelinType(
        fullyQualifiedJavelinName = Array.JAVELIN_TYPE_NAME
)
public final class Array implements Primitive {

    static final String JAVELIN_TYPE_NAME = "javelin.shaft.Array";
    public static final SimpleType TYPE = new SimpleType(Span.undefined(), JAVELIN_TYPE_NAME);

    private final Object[] value;
    private final CollectionType type;

    @Ignore
    private Array(Object[] value, Type elementType) {
        this.value = value;
        this.type = new CollectionType(Span.undefined(), U.TYPE, elementType);
    }

    @Ignore
    public static Array of(Object[] value, Type elementType) {
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
    public Type type() {
        return TYPE;
    }

}
