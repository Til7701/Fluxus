package de.til7701.fluxus.ast;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public sealed interface Statement
        extends Node
        permits Expression,
        Statement.Assignment,
        Statement.Import,
        Statement.Return,
        Statement.StatementList,
        Statement.VariableInitialization,
        Statement.When {

    String toSingleLineCode();

    record Assignment(Span span,
                      Expression target, Expression value
    ) implements Statement {
        @Override
        public String toSingleLineCode() {
            return target.toSingleLineCode() + " = " + value.toSingleLineCode();
        }
    }

    record Import(Span span,
                  String typeToImport, @Nullable String asName
    ) implements Statement {
        @Override
        public String toSingleLineCode() {
            return "import" + typeToImport + (asName == null ? "" : " as " + asName);
        }
    }

    record Return(Span span,
                  @Nullable Expression value
    ) implements Statement {
        @Override
        public String toSingleLineCode() {
            return "return" + (value == null ? "" : " " + value.toSingleLineCode());
        }
    }

    record StatementList(Span span,
                         List<Statement> statements
    ) implements Statement {
        @Override
        public String toSingleLineCode() {
            return String.join("; ", statements.stream()
                    .map(Statement::toSingleLineCode)
                    .toArray(String[]::new));
        }
    }

    record VariableInitialization(Span span,
                                  String name, Expression value
    ) implements Statement {
        @Override
        public String toSingleLineCode() {
            return name + " = " + value.toSingleLineCode();
        }
    }

    record When(long id, Span span,
                boolean evalInstantly, Expression condition, Statement body
    ) implements Statement {

        private static final AtomicLong idProvider = new AtomicLong(0);

        public When(Span span, boolean evalInstantly, Expression condition, Statement body) {
            this(idProvider.getAndIncrement(), span, evalInstantly, condition, body);
        }

        @Override
        public String toSingleLineCode() {
            return (evalInstantly ? "do " : "") + "when " + condition.toSingleLineCode() + " " + body.toSingleLineCode();
        }
    }


}
