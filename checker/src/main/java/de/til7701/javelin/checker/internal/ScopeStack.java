package de.til7701.javelin.checker.internal;

import de.til7701.javelin.ast.type.Type;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

public class ScopeStack {

    private final Deque<Scope> stack = new ArrayDeque<>();

    public void push(Scope scope) {
        stack.push(scope);
    }

    public Scope pop() {
        return stack.pop();
    }

    public void initializeVariable(String name, Type type) {
        Scope scope = stack.peek();
        scope.initializeVariable(name, type);
    }

    public @Nullable Type getVariable(String name) {
        for (Scope scope : stack) {
            Type type = scope.getVariable(name);
            if (type != null) {
                return type;
            }
        }
        return null;
    }

}
