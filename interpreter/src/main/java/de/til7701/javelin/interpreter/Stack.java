package de.til7701.javelin.interpreter;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

@ToString
@EqualsAndHashCode
public final class Stack {

    private final Deque<StackFrame> stack;

    Stack() {
        this(new ArrayDeque<>());
    }

    private Stack(Deque<StackFrame> stack) {
        this.stack = stack;
    }

    void push(StackFrame stackFrame) {
        stack.push(stackFrame);
    }

    @Nullable StackFrame pop() {
        if (stack.isEmpty())
            return null;
        return stack.pop();
    }

    @Nullable StackFrame peek() {
        return stack.peek();
    }

    void initializeVariable(String name, Variable variable) {
        StackFrame stackFrame = stack.peek();
        if (stackFrame == null) {
            stackFrame = new StackFrame();
            push(stackFrame);
        }
        stackFrame.initializeVariable(name, variable);
    }

    @Nullable Variable getVariable(String name) {
        for (StackFrame stackFrame : stack) {
            Variable variable = stackFrame.getVariable(name);
            if (variable != null) {
                return variable;
            }
        }
        return null;
    }

    Stack snapshot() {
        Deque<StackFrame> snapshot = stack.stream()
                .map(StackFrame::snapshot)
                .collect(new Collector<StackFrame, ArrayDeque<StackFrame>, Deque<StackFrame>>() {

                    @Override
                    public Supplier<ArrayDeque<StackFrame>> supplier() {
                        return ArrayDeque::new;
                    }

                    @Override
                    public BiConsumer<ArrayDeque<StackFrame>, StackFrame> accumulator() {
                        return ArrayDeque::push;
                    }

                    @Override
                    public BinaryOperator<ArrayDeque<StackFrame>> combiner() {
                        return (left, right) -> {
                            left.addAll(right);
                            return left;
                        };
                    }

                    @Override
                    public Function<ArrayDeque<StackFrame>, Deque<StackFrame>> finisher() {
                        return array -> array;
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return Set.of();
                    }
                });
        return new Stack(snapshot);
    }

    public void print(PrintStream out) {
        for (StackFrame frame : this.stack) {
            out.println("    " + frame);
        }
    }

}
