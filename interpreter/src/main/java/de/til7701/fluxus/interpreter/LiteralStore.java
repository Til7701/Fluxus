package de.til7701.fluxus.interpreter;

import de.til7701.fluxus.common.fons.Bool;
import de.til7701.fluxus.common.fons.I;
import de.til7701.fluxus.common.fons.U;
import de.til7701.fluxus.common.util.ints.IN;
import de.til7701.fluxus.common.util.ints.UN;

import java.util.HashMap;
import java.util.Map;

public final class LiteralStore {

    private final Map<Boolean, Variable> booleans = new HashMap<>();
    private final Map<IN, Variable> signedIntegers = new HashMap<>();
    private final Map<UN, Variable> unsignedIntegers = new HashMap<>();

    Variable forBool(boolean literal) {
        return booleans.computeIfAbsent(literal, key -> new PrimitiveVariable(Bool.of(key)));
    }

    Variable forI(long value, int bitCount) {
        IN in = IN.of(value, bitCount);
        return signedIntegers.computeIfAbsent(in, _ -> new PrimitiveVariable(I.of(in)));
    }

    Variable forU(long value, int bitCount) {
        UN un = UN.of(value, bitCount);
        return unsignedIntegers.computeIfAbsent(un, _ -> new PrimitiveVariable(U.of(un)));
    }

    public String valuesToString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Bool: [true=")
                .append(booleans.get(true))
                .append(", false=")
                .append(booleans.get(false))
                .append("]");
        sb.append("\n");

        sb.append("I: [");
        for (Map.Entry<IN, Variable> entry : signedIntegers.entrySet()) {
            sb.append(entry.getKey()).append("I").append(entry.getKey().getBitCount());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append(", ");
        }
        sb.append("]\n");

        sb.append("U: [");
        for (Map.Entry<UN, Variable> entry : unsignedIntegers.entrySet()) {
            sb.append(entry.getKey()).append("U").append(entry.getKey().getBitCount());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append(", ");
        }
        sb.append("]");

        return sb.toString();
    }

}
