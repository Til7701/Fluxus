package de.til7701.fluxus.interpreter;

import de.til7701.fluxus.common.NotImplementedException;
import de.til7701.fluxus.common.fons.Array;
import de.til7701.fluxus.common.fons.Primitive;
import de.til7701.fluxus.common.fons.Str;
import de.til7701.fluxus.common.fons.Void;
import de.til7701.fluxus.common.type.TypeDescriptor;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

final class VariableFactory {

    private static final Variable VOID = new PrimitiveVariable(Void.of());
    @Getter
    private final LiteralStore literalStore = new LiteralStore();

    Variable fromBoolLiteral(boolean value) {
        return literalStore.forBool(value);
    }

    Variable fromStrLiteral(String value) {
        return new PrimitiveVariable(Str.of(value));
    }

    Variable fromILiteral(long value, int bitCount) {
        return literalStore.forI(value, bitCount);
    }

    Variable fromULiteral(long value, int bitCount) {
        return literalStore.forU(value, bitCount);
    }

    Variable asArray(Variable[] variables, TypeDescriptor elementType) {
        return new PrimitiveVariable(Array.of(variables, elementType));
    }

    Variable fromJavaValue(@Nullable Object result) {
        return switch (result) {
            case Variable variable -> variable;
            case null -> VOID;
            case Primitive primitive -> new PrimitiveVariable(primitive);
            default -> throw new NotImplementedException(result.getClass().toString());
        };
    }

}
