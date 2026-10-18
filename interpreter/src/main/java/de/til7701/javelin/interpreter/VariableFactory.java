package de.til7701.javelin.interpreter;

import de.til7701.javelin.common.NotImplementedException;
import de.til7701.javelin.common.shaft.*;
import de.til7701.javelin.common.shaft.Void;
import de.til7701.javelin.common.type.TypeDescriptor;
import de.til7701.javelin.common.util.ints.IN;
import de.til7701.javelin.common.util.ints.UN;
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
        return new PrimitiveVariable(I.of(IN.of(value, bitCount)));
    }

    Variable fromULiteral(long value, int bitCount) {
        return new PrimitiveVariable(U.of(UN.of(value, bitCount)));
    }

    Variable asArray(Variable[] variables, TypeDescriptor elementType) {
        return new PrimitiveVariable(Array.of(variables, elementType));
    }

    String literalsToString() {
        return literalStore.valuesToString();
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
