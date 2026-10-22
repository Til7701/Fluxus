package de.til7701.fluxus.interpreter;

import de.til7701.fluxus.common.fons.Primitive;
import de.til7701.fluxus.common.type.TypeDescriptor;

final class PrimitiveVariable extends AbstractVariable {

    private Primitive value;

    PrimitiveVariable(Primitive value) {
        this.value = value;
    }

    @Override
    public TypeDescriptor descriptor() {
        return value.descriptor();
    }

    @Override
    public void set(Variable variable) {
        if (variable instanceof PrimitiveVariable primitive) {
            this.value = primitive.value;
        } else {
            throw new IllegalArgumentException();
        }
        emitChange();
    }

    @Override
    public Variable createNew() {
        return new PrimitiveVariable(value);
    }

    @Override
    public Object javaValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
