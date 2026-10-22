package de.til7701.fluxus.ast.statement;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.expression.Expression;

import java.util.concurrent.atomic.AtomicLong;

public record WhenStatement(
        long id,
        Span span,
        boolean evalInstantly,
        Expression condition,
        Statement body
) implements Statement {

    private static final AtomicLong idProvider = new AtomicLong(0);

    public WhenStatement(Span span, boolean evalInstantly, Expression condition, Statement body) {
        this(idProvider.getAndIncrement(), span, evalInstantly, condition, body);
    }

}
