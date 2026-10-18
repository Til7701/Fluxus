package de.til7701.javelin.interpreter;

import de.til7701.javelin.common.shaft.Bool;

import java.util.HashMap;
import java.util.Map;

public final class LiteralStore {

    private final Map<Boolean, Variable> booleans = new HashMap<>();

    Variable forBool(boolean literal) {
        return booleans.computeIfAbsent(literal, key -> new PrimitiveVariable(Bool.of(key)));
    }

    public String valuesToString() {
        return "Bool: [ true = " + booleans.get(true) + ", false = " + booleans.get(false) + " ]";
    }

}
