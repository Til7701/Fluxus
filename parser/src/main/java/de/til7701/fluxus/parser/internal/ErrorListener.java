package de.til7701.fluxus.parser.internal;

import de.til7701.fluxus.parser.SyntaxErrorException;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.jspecify.annotations.NullMarked;

@NullMarked
@RequiredArgsConstructor
public final class ErrorListener extends BaseErrorListener {

    private final String sourceName;

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw new SyntaxErrorException(sourceName, offendingSymbol, line, charPositionInLine, msg, e);
    }

}
