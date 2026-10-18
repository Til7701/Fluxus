package de.til7701.javelin.interpreter;

import de.til7701.javelin.common.shaft.Void;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@ToString
@EqualsAndHashCode
public final class StackFrame {

    private final Map<String, Variable> variables;
    @Setter
    @Getter
    private Variable returnValue = new PrimitiveVariable(Void.of());
    @Getter
    @Setter
    private boolean executingInterrupt;

    StackFrame() {
        this(new HashMap<>());
    }

    private StackFrame(Map<String, Variable> variables) {
        this.variables = variables;
    }

    void initializeVariable(String name, Variable variable) {
        variables.put(name, variable);
    }

    @Nullable Variable getVariable(String name) {
        return variables.get(name);
    }

    StackFrame snapshot() {
        Map<String, Variable> snapshot = new HashMap<>(variables);
        return new StackFrame(snapshot);
    }

}
