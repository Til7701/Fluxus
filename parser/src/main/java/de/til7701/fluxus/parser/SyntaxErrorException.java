package de.til7701.fluxus.parser;

import lombok.Getter;
import org.jspecify.annotations.NullMarked;

@Getter
@NullMarked
public final class SyntaxErrorException extends RuntimeException {

    private final String sourceName;
    private final transient Object offendingSymbol;
    private final int line;
    private final int charPositionInLine;
    private final String msg;

    public SyntaxErrorException(String sourceName, Object offendingSymbol, int line, int charPositionInLine, String msg, Throwable cause) {
        super(cause);
        this.sourceName = sourceName;
        this.offendingSymbol = offendingSymbol;
        this.line = line;
        this.charPositionInLine = charPositionInLine;
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return "[ERROR] " + sourceName + " line " + line + ": " + msg;
    }

}
