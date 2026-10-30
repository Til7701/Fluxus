package de.til7701.fluxus.common.fons;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.fons.util.*;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.type.trait.CollectionTrait;
import de.til7701.fluxus.common.type.trait.UTrait;
import de.til7701.fluxus.common.util.Ignore;
import de.til7701.fluxus.n.UN;

import java.util.Arrays;

@FluxusDoc("""
        The `Array` class is usually referred to as `T[U31]` where `T` is the element type.
        
        Arrays can be instantiated in two ways:
        
        ```fluxus
        let a1 = ["x", "y", "z"];
        let a2 = ["x"; 10U31];
        ```
        
        Both are of type `Str[U31]` where `Str` is `fluxus.flumen.Str`.
        The first is called a literal array creation and may be useful in some cases, but the second one (simply called
        array creation) is the more useful one, since the length can be any expression evaluated at runtime.
        Since there is no `null` pointer in Fluxus, you need to provide a default value, which will be copied,
        like with the `new` expression for each entry.
        
        You may think that `U31` is an unusual type to index an array. However, this is a technical limitation in Java
        that most don't think about in that way.
        
        You can read and write like you would most likely expect as long as you expect that array indices start at 0.
        
        ```fluxus
        let x = a1[0U31];
        a1[0U31] = "1";
        ```
        """)
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

    @FluxusDoc("""
            Returns the element at the location `index`. The index must be of type `U31`.
            
            Overloads: `array[index]`.
            """)
    public static Object index(@FluxusArgName("array") Array array, @FluxusArgName("index") U index) {
        int i = (int) index.getValue().getValue();
        return array.value[i];
    }

    @FluxusDoc("""
            Converts the array to a string.
            Note that currently the elements are not converted to string correctly if it is not a part of Fons.
            
            Overloads: `array as Str`.
            """)
    public static String as__fluxus_flumen_Str(@FluxusArgName("value") Array value, @FluxusArgName("ignored") PType ignored) {
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
