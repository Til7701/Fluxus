package de.til7701.fluxus.ast;

import java.util.List;

public sealed interface Expression extends Statement {

    record BooleanLiteral(Span span,
                          boolean value
    ) implements Expression {
    }

    record SignedIntegerLiteral(Span span,
                                long value, long bitCount
    ) implements Expression {
    }

    record UnsignedIntegerLiteral(Span span,
                                  long value, long bitCount
    ) implements Expression {
    }

    record FloatLiteral(Span span,
                        double value, long bitCount
    ) implements Expression {
    }

    record CharLiteral(Span span,
                       String value
    ) implements Expression {
    }

    record StringLiteral(Span span,
                         String value
    ) implements Expression {
    }

    record Symbol(Span span,
                  String identifier
    ) implements Expression {
    }

    record New(Span span,
               Expression expression
    ) implements Expression {
    }

    record TypeCast(Span span,
                    Type targetType, Expression expression
    ) implements Expression {
    }

    record InstanceFieldAccess(Span span,
                               Expression instance, String fieldName
    ) implements Expression {
    }

    record StaticFieldAccess(Span span,
                             Type type, String fieldName
    ) implements Expression {
    }

    record LeftUnary(Span span,
                     LeftUnaryOperator operator, Expression expression
    ) implements Expression {
    }

    enum LeftUnaryOperator {
        SUB,
        NOT,
        HASH,
    }

    record Binary(Span span,
                  Expression left, BinaryOperator operator, Expression right
    ) implements Expression {
    }

    enum BinaryOperator {
        ADD,
        SUB,
        MUL,
        DIVIDE,
        LT,
        GT,
        LTE,
        GTE,
        EQ,
        NEQ,
        INDEX,
        AND,
        OR,
        IMPL,
        BIT_AND,
        BIT_OR,
        BIT_XOR,
        SHIFT_LEFT,
        SHIFT_RIGHT
    }

    record ArrayCreation(Span span,
                         Expression defaultValue, Expression length
    ) implements Expression {
    }

    record ArrayLiteralCreation(Span span,
                                List<Expression> values
    ) implements Expression {
    }

    record InstanceMethodCall(Span span,
                              Expression instance, String methodName, List<Expression> arguments
    ) implements Expression {
    }

    record StaticMethodCall(Span span,
                            Type type, String methodName, List<Expression> arguments
    ) implements Expression {
    }

    record ConstructorCall(Span span,
                           Type type, List<Expression> arguments
    ) implements Expression {
    }

}
