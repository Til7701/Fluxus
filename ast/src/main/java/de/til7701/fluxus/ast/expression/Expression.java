package de.til7701.fluxus.ast.expression;

import de.til7701.fluxus.ast.statement.Statement;

public sealed interface Expression extends Statement permits ArrayCreation, ArrayLiteralCreation, BinaryExpression, BooleanLiteralExpression, CharLiteralExpression, ConstructorCall, FloatLiteralExpression, InstanceFieldAccess, InstanceMethodCall, LeftUnaryExpression, NewExpression, SignedIntegerLiteralExpression, StaticFieldAccess, StaticMethodCall, StringLiteralExpression, SymbolExpression, TypeCastExpression, UnsignedIntegerLiteralExpression {
}
