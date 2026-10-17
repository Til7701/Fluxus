package de.til7701.javelin.interpreter.variable;


import de.til7701.javelin.common.type.TypeDescriptor;

public interface Variable {

    TypeDescriptor descriptor();

    /// Sets the value of the variable using the given variable with copy semantics
    void set(Variable variable);

    Variable createNew();

    Object javaValue();

    void listen(Runnable onUpdate);

}
