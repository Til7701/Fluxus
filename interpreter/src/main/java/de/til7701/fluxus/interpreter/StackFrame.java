package de.til7701.fluxus.interpreter;

import de.til7701.fluxus.ast.Span;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.util.*;

@EqualsAndHashCode
public final class StackFrame {

    private final Map<String, Variable> variables;
    @Getter
    private final Deque<Interrupt> interruptQueue;
    @Getter
    private final List<When> whens;
    @Getter
    private final @Nullable Span createdAt;

    StackFrame(@Nullable Span createdAt) {
        this(new HashMap<>(), new ArrayDeque<>(), new ArrayList<>(), createdAt);
    }

    private StackFrame(Map<String, Variable> variables, Deque<Interrupt> interruptQueue, List<When> whens, @Nullable Span createdAt) {
        this.variables = variables;
        this.interruptQueue = interruptQueue;
        this.whens = whens;
        this.createdAt = createdAt;
    }

    void initializeVariable(String name, Variable variable) {
        variables.put(name, variable);
    }

    @Nullable Variable getVariable(String name) {
        return variables.get(name);
    }

    StackFrame snapshot() {
        Map<String, Variable> snapshot = new HashMap<>(variables);
        return new StackFrame(snapshot, interruptQueue, whens, createdAt);
    }

    void destroy() {
        for (When when : whens)
            when.destroy();
    }

    @Override
    public String toString() {
        return createdAt + " variables="
                + variables
                + " interrupts: " + interruptQueue.stream().map(Interrupt::mutexID).toList()
                + " whens: " + whens.stream().map(w -> w.getCreationFrame().getCreatedAt()).toList()
                ;
    }

}
