package de.til7701.javelin.interpreter;

@FunctionalInterface
interface VariableObserver {

    void run(Variable variable);

}
