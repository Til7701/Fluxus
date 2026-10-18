package de.til7701.javelin.ast.expression;

import de.til7701.javelin.ast.statement.Statement;

public sealed interface Expression extends Statement permits ArrayCreation, ArrayLiteralCreation, BinaryExpression, BooleanLiteralExpression, ConstructorCall, FloatLiteralExpression, InstanceFieldAccess, InstanceMethodCall, LeftUnaryExpression, NewExpression, RightUnaryExpression, SignedIntegerLiteralExpression, StaticFieldAccess, StaticMethodCall, StringLiteralExpression, SymbolExpression, TypeCastExpression, UnsignedIntegerLiteralExpression {
}
