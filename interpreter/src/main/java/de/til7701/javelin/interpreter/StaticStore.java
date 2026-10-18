package de.til7701.javelin.interpreter;

import de.til7701.javelin.common.type.TypeDescriptor;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

final class StaticStore {

    private final Map<TypeDescriptor, Map<String, Variable>> statics = new HashMap<>();

    boolean has(TypeDescriptor typeDescriptor) {
        return statics.containsKey(typeDescriptor);
    }

    void init(TypeDescriptor typeDescriptor, Map<String, Variable> values) {
        statics.put(typeDescriptor, values);
    }

    @Nullable Variable get(TypeDescriptor typeDescriptor, String fieldName) {
        Map<String, Variable> variables = statics.get(typeDescriptor);
        if (variables == null) return null;
        return variables.get(fieldName);
    }

}
