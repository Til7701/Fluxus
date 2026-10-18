package de.til7701.javelin.interpreter;


import de.til7701.javelin.common.type.TypeDescriptor;

sealed interface Variable permits PrimitiveVariable, StructVariable {

    TypeDescriptor descriptor();

    /// Sets the value of the variable using the given variable with copy semantics
    void set(Variable variable);

    Variable createNew();

    Object javaValue();

    void listen(Runnable onUpdate);

}
