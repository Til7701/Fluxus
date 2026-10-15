package de.til7701.javelin.checker.internal;

import de.til7701.javelin.ast.type.Type;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Scope {

    private final Map<String, Type> variables = new HashMap<>();

    public void initializeVariable(String name, Type type) {
        variables.put(name, type);
    }

    public @Nullable Type getVariable(String name) {
        return variables.get(name);
    }

}
