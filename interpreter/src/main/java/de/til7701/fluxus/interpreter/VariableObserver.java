package de.til7701.fluxus.interpreter;

@FunctionalInterface
interface VariableObserver {

    void run(Variable variable);

}
