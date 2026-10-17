package de.til7701.javelin.interpreter.variable;

import de.til7701.javelin.common.type.TypeDescriptor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class StructVariable implements Variable {

    private final Collection<Runnable> listeners = new CopyOnWriteArrayList<>();

    private final TypeDescriptor typeDescriptor;
    private final Map<String, Variable> instanceFields;

    public StructVariable(TypeDescriptor typeDescriptor, Map<String, Variable> instanceFields) {
        this.typeDescriptor = typeDescriptor;
        this.instanceFields = new HashMap<>(instanceFields);
        instanceFields.forEach((_, variable) -> variable.listen(() -> listeners.forEach(Runnable::run)));
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
    public void listen(Runnable onUpdate) {
        listeners.add(onUpdate);
    }

    @Override
    public TypeDescriptor descriptor() {
        return typeDescriptor;
    }

    public Variable getField(String fieldName) {
        return instanceFields.get(fieldName);
    }
}
