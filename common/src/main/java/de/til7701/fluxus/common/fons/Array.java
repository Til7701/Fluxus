package de.til7701.fluxus.common.fons;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.fons.util.FluxusDoc;
import de.til7701.fluxus.common.fons.util.FluxusType;
import de.til7701.fluxus.common.fons.util.Primitive;
import de.til7701.fluxus.common.fons.util.Primitives;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.type.trait.CollectionTrait;
import de.til7701.fluxus.common.type.trait.UTrait;
import de.til7701.fluxus.common.util.Ignore;
import de.til7701.fluxus.common.util.n.UN;

import java.util.Arrays;

@FluxusType(
        fullyQualifiedFluxusName = Array.FLUXUS_TYPE_NAME
)
public final class Array implements Primitive {

    static final String FLUXUS_TYPE_NAME = "fluxus.fons.Array";
    public static final Type.Simple TYPE = new Type.Simple(Span.undefined(), FLUXUS_TYPE_NAME);

    public static final LazyConstant<TypeDescriptor> DESCRIPTOR = LazyConstant.of(() -> Primitives.loadJavaClass(Array.class, TYPE));

    private final Object[] value;
    private final CollectionTrait descriptor;

    @Ignore
    private Array(Object[] value, TypeDescriptor elementType) {
        this.value = value;
        this.descriptor = new CollectionTrait(new UTrait(31), elementType);
    }

    @Ignore
    public static Array of(Object[] value, TypeDescriptor elementType) {
        return new Array(value, elementType);
    }

    @FluxusDoc("""
            Returns the length of the array.
            """)
    public U length() {
        return U.of(UN.of(value.length, 31));
    }

    public static Object index(Array array, U index) {
        int i = (int) index.getValue().getValue();
        return array.value[i];
    }

    public static String as__fluxus_flumen_Str(Array value, PType ignored) {
        return Arrays.deepToString(value.value);
    }

    @Ignore
    @Override
    public TypeDescriptor descriptor() {
        return DESCRIPTOR.get();
    }

    @Ignore
    @Override
    public String toString() {
        return Arrays.deepToString(value);
    }

}
