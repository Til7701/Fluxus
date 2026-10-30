package de.til7701.fluxus.common.fons;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.fons.util.*;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.util.Ignore;
import de.til7701.fluxus.n.IN;
import lombok.Getter;

@FluxusDoc("""
        A character like `'c'`.
        """)
@FluxusType(
        fullyQualifiedFluxusName = Char.FLUXUS_TYPE_NAME
)
public final class Char implements Primitive {

    static final String FLUXUS_TYPE_NAME = "fluxus.fons.Char";
    public static final Type.Simple TYPE = new Type.Simple(Span.undefined(), FLUXUS_TYPE_NAME);

    public static final LazyConstant<TypeDescriptor> DESCRIPTOR = LazyConstant.of(() -> Primitives.loadJavaClass(Char.class, TYPE));

    @Getter(onMethod_ = {@Ignore})
    private final char value;

    @Ignore
    private Char(char value) {
        this.value = value;
    }

    @Ignore
    public static Char of(char value) {
        return new Char(value);
    }

    @FluxusDoc("""
            Checks if two `Char`s are equal.
            
            Overloads: `a == b`.
            """)
    public static Bool eq(@FluxusArgName("left") Char left, @FluxusArgName("right") Char right) {
        return Bool.of(left.value == right.value);
    }

    @FluxusDoc("""
            Checks if two `Char`s are not equal.
            
            Overloads: `a == b`.
            """)
    public static Bool neq(@FluxusArgName("left") Char left, @FluxusArgName("right") Char right) {
        return Bool.of(left.value != right.value);
    }

    @FluxusDoc("""
            Returns `true`, if the `Char` is blank (i.e. whitespace). `false` otherwise.
            """)
    public Bool isBlank() {
        return Bool.of(Character.isWhitespace(this.value));
    }

    @FluxusDoc("""
            Returns `true`, if the `Char` is a decimal digit. `false` otherwise.
            """)
    public Bool isDigit() {
        return Bool.of(Character.isDigit(this.value));
    }

    @FluxusDoc("""
            Returns the `Char` as a decimal digit.
            """)
    public I asDigit() {
        return I.of(IN.of(Character.digit(this.value, 10), 64));
    }

    @FluxusDoc("""
            Converts the `Char` to a string.
            
            Overloads: `c as Str`.
            """)
    public static String as__fluxus_flumen_Str(@FluxusArgName("value") Char value, @FluxusArgName("ignored") PType ignored) {
        return String.valueOf(value.value);
    }

    @Ignore
    @Override
    public TypeDescriptor descriptor() {
        return DESCRIPTOR.get();
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
