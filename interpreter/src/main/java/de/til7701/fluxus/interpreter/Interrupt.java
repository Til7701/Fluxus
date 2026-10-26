package de.til7701.fluxus.interpreter;

import de.til7701.fluxus.ast.Statement;
import de.til7701.fluxus.common.environment.Imports;

record Interrupt(
        long mutexID,
        Statement unterbrechungsbehandlungsprozedur,
        Stack stack,
        Imports imports
) {
}
