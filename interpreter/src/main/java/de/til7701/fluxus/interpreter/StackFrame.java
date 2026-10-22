package de.til7701.fluxus.interpreter;

import de.til7701.fluxus.common.fons.Void;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.util.*;

@EqualsAndHashCode
public final class StackFrame {

    private final Map<String, Variable> variables;
    @Setter
    @Getter
    private Variable returnValue = new PrimitiveVariable(Void.of());
    @Getter
    private final Deque<Interrupt> interruptQueue;
    @Getter
    private final List<When> whens;

    StackFrame() {
        this(new HashMap<>(), new ArrayDeque<>(), new ArrayList<>());
    }

    private StackFrame(Map<String, Variable> variables, Deque<Interrupt> interruptQueue, List<When> whens) {
        this.variables = variables;
        this.interruptQueue = interruptQueue;
        this.whens = whens;
    }

    void initializeVariable(String name, Variable variable) {
        variables.put(name, variable);
    }

    @Nullable Variable getVariable(String name) {
        return variables.get(name);
    }

    StackFrame snapshot() {
        Map<String, Variable> snapshot = new HashMap<>(variables);
        return new StackFrame(snapshot, interruptQueue, whens);
    }

    void destroy() {
        for (When when : whens)
            when.destroy();
    }

    @Override
    public String toString() {
        return "StackFrame{" +
                "variables=" + variables +
                ", returnValue=" + returnValue +
                '}';
    }

}
