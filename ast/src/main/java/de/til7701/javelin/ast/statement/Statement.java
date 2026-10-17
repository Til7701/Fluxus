package de.til7701.javelin.ast.statement;

import de.til7701.javelin.ast.Node;
import de.til7701.javelin.ast.expression.Expression;

public sealed interface Statement extends Node permits Expression, Assignment, Import, ReturnStatement, StatementList, VariableInitialization, WhenStatement {
}
