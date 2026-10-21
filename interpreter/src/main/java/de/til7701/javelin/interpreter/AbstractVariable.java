package de.til7701.javelin.interpreter;

import java.util.LinkedHashSet;
import java.util.Set;

abstract sealed class AbstractVariable implements Variable permits PrimitiveVariable, StructVariable {

    private final Set<VariableObserver> observers = new LinkedHashSet<>();

    @Override
    public void listen(VariableObserver onUpdate) {
        observers.add(onUpdate);
    }

    @Override
    public void stopListening(VariableObserver obs) {
        observers.remove(obs);
    }

    protected void emitChange() {
        for (VariableObserver observer : observers)
            observer.run(this);
    }

}
