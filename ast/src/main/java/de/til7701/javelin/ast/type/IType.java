package de.til7701.javelin.ast.type;

import de.til7701.javelin.ast.Span;

import java.util.function.Function;

public record IType(
        Span span,
        int bitCount
) implements Type {

    @Override
    public Type mapNames(Function<String, String> nameMapper) {
        return this;
    }

    @Override
    public boolean isAssignableTo(Type other) {
        return (other instanceof IType(_, int otherBitCount)
                && this.bitCount == otherBitCount)
                || (other instanceof SimpleType(_, String name)
                && name.equals("javelin.shaft.I"));
    }

    @Override
    public String name() {
        return "javelin.shaft.I";
    }

}
