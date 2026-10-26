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

    record Assignment(Span span,
                      Expression target, Expression value
    ) implements Statement {
    }

    record Import(Span span,
                  String typeToImport, @Nullable String asName
    ) implements Statement {
    }

    record Return(Span span,
                  @Nullable Expression value
    ) implements Statement {
    }

    record StatementList(Span span,
                         List<Statement> statements
    ) implements Statement {
    }

    record VariableInitialization(Span span,
                                  String name, Expression value
    ) implements Statement {
    }

    record When(long id, Span span,
                boolean evalInstantly, Expression condition, Statement body
    ) implements Statement {

        private static final AtomicLong idProvider = new AtomicLong(0);

        public When(Span span, boolean evalInstantly, Expression condition, Statement body) {
            this(idProvider.getAndIncrement(), span, evalInstantly, condition, body);
        }

    }


}
