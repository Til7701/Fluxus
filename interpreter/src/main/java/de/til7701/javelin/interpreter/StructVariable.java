package de.til7701.javelin.interpreter;

import de.til7701.javelin.common.type.TypeDescriptor;

import java.util.HashMap;
import java.util.Map;

final class StructVariable extends AbstractVariable implements VariableObserver {

    private final TypeDescriptor typeDescriptor;
    private final Map<String, Variable> instanceFields;

    StructVariable(TypeDescriptor typeDescriptor, Map<String, Variable> instanceFields) {
        this.typeDescriptor = typeDescriptor;
        this.instanceFields = new HashMap<>(instanceFields);
        instanceFields.forEach((_, variable) -> variable.listen(this));
    }

    @Override
    public void set(Variable variable) {
        if (variable instanceof StructVariable struct) {
            instanceFields.clear();
            instanceFields.putAll(struct.instanceFields);
        }
    }

    @Override
    public Object javaValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Variable createNew() {
        return new StructVariable(typeDescriptor, instanceFields);
    }


    @Override
    public TypeDescriptor descriptor() {
        return typeDescriptor;
    }

    public Variable getField(String fieldName) {
        return instanceFields.get(fieldName);
    }

    @Override
    public String toString() {
        return typeDescriptor.type().name() + "{" +
                instanceFields +
                '}';
    }

    @Override
    public void run(Variable variable) {
        emitChange();
    }

}
