package de.til7701.fluxus.ast.statement;

import de.til7701.fluxus.ast.Node;
import de.til7701.fluxus.ast.expression.Expression;

public sealed interface Statement extends Node permits Expression, Assignment, Import, ReturnStatement, StatementList, VariableInitialization, WhenStatement {
}
