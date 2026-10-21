package de.til7701.javelin.interpreter;

import de.til7701.javelin.ast.statement.Statement;
import de.til7701.javelin.common.environment.Imports;

record Interrupt(
        long mutexID,
        Statement unterbrechungsbehandlungsprozedur,
        Stack stack,
        Imports imports
) {
}
