package de.til7701.javelin.interpreter.variable;

import de.til7701.javelin.ast.type.Type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class StructVariable implements Variable {

    private final Collection<Runnable> listeners = new CopyOnWriteArrayList<>();

    private final Type type;
    private final Map<String, Variable> instanceFields;

    public StructVariable(Type type, Map<String, Variable> instanceFields) {
        this.type = type;
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
        return new StructVariable(type, instanceFields);
    }

    @Override
    public void listen(Runnable onUpdate) {
        listeners.add(onUpdate);
    }

    @Override
    public Type type() {
        return type;
    }

    public Variable getField(String fieldName) {
        return instanceFields.get(fieldName);
    }
}
