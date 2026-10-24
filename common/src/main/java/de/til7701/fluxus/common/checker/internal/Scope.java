package de.til7701.fluxus.common.checker.internal;

import de.til7701.fluxus.common.type.TypeDescriptor;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Scope {

    private final Map<String, TypeDescriptor> variables = new HashMap<>();

    public void initializeVariable(String name, TypeDescriptor type) {
        variables.put(name, type);
    }

    public @Nullable TypeDescriptor getVariable(String name) {
        return variables.get(name);
    }

}
