package de.til7701.fluxus.ast;

import de.til7701.fluxus.n.FN;
import de.til7701.fluxus.n.IN;
import de.til7701.fluxus.n.UN;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public sealed interface Expression extends Statement {

    record BooleanLiteral(Span span,
                          boolean value
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return Boolean.toString(value);
        }
    }

    record SignedIntegerLiteral(Span span,
                                long value, long bitCount
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return IN.of(value, bitCount) + "I" + bitCount;
        }
    }

    record UnsignedIntegerLiteral(Span span,
                                  long value, long bitCount
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return UN.of(value, bitCount) + "U" + bitCount;
        }
    }

    record FloatLiteral(Span span,
                        double value, long bitCount
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return FN.of(value, bitCount) + "F" + bitCount;
        }
    }

    record CharLiteral(Span span,
                       String value
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return "'" + value + "'";
        }
    }

    record StringLiteral(Span span,
                         String value
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return "\"" + value + "\"";
        }
    }

    record Symbol(Span span,
                  String identifier
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return identifier;
        }
    }

    record New(Span span,
               Expression expression
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return "new " + expression.toSingleLineCode();
        }
    }

    record TypeCast(Span span,
                    Type targetType, Expression expression
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return expression.toSingleLineCode() + " as " + targetType.toCode();
        }
    }

    record InstanceFieldAccess(Span span,
                               Expression instance, String fieldName
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return instance.toSingleLineCode() + "." + fieldName;
        }
    }

    record StaticFieldAccess(Span span,
                             Type type, String fieldName
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return type.toCode() + "." + fieldName;
        }
    }

    record LeftUnary(Span span,
                     LeftUnaryOperator operator, Expression expression
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return operator.getValue() + expression.toSingleLineCode();
        }
    }

    @Getter
    @RequiredArgsConstructor
    enum LeftUnaryOperator {
        SUB("-"),
        NOT("!"),
        HASH("#");

        private final String value;
    }

    record Binary(Span span,
                  Expression left, BinaryOperator operator, Expression right
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            if (operator == BinaryOperator.INDEX) {
                return left.toSingleLineCode() + "[" + right.toSingleLineCode() + "]";
            } else {
                return left.toSingleLineCode() + " " + operator.getValue() + " " + right.toSingleLineCode();
            }
        }
    }

    @Getter
    @RequiredArgsConstructor
    enum BinaryOperator {
        ADD("+"),
        SUB("-"),
        MUL("*"),
        DIVIDE("/"),
        LT("<"),
        GT(">"),
        LTE("<="),
        GTE(">="),
        EQ("=="),
        NEQ("!="),
        INDEX(""),
        AND("&&"),
        OR("||"),
        IMPL("==>"),
        BIT_AND("&"),
        BIT_OR("|"),
        BIT_XOR("^"),
        SHIFT_LEFT("<<"),
        SHIFT_RIGHT(">>");

        private final String value;
    }

    record ArrayCreation(Span span,
                         Expression defaultValue, Expression length
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return "[ " + defaultValue.toSingleLineCode() + "; " + length.toSingleLineCode() + " ]";
        }
    }

    record ArrayLiteralCreation(Span span,
                                List<Expression> values
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return "[" + String.join(", ", values.stream()
                    .map(Expression::toSingleLineCode)
                    .toArray(String[]::new)) + "]";
        }
    }

    record InstanceMethodCall(Span span,
                              Expression instance, String methodName, List<Expression> arguments
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return instance.toSingleLineCode() + "." + methodName + "(" + String.join(", ", arguments.stream()
                    .map(Expression::toSingleLineCode)
                    .toArray(String[]::new)) + ")";
        }
    }

    record StaticMethodCall(Span span,
                            Type type, String methodName, List<Expression> arguments
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return type.simpleName() + "." + methodName + "(" + String.join(", ", arguments.stream()
                    .map(Expression::toSingleLineCode)
                    .toArray(String[]::new)) + ")";
        }
    }

    record ConstructorCall(Span span,
                           Type type, List<Expression> arguments
    ) implements Expression {
        @Override
        public String toSingleLineCode() {
            return type.simpleName() + "(" + String.join(", ", arguments.stream()
                    .map(Expression::toSingleLineCode)
                    .toArray(String[]::new)) + ")";
        }
    }

}
