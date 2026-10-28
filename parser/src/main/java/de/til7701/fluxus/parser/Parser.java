package de.til7701.fluxus.parser;

import de.til7701.fluxus.ast.Ast;
import de.til7701.fluxus.ast.Node;
import de.til7701.fluxus.parser.internal.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@NullMarked
public final class Parser {

    public Ast parseFile(File file) throws IOException {
        CharStream charStream = CharStreams.fromFileName(file.getAbsolutePath());
        return parse(charStream, file.getName());
    }

    public Ast parseInputStream(InputStream stream, String sourceName) throws IOException {
        CharStream charStream = CharStreams.fromStream(stream);
        return parse(charStream, sourceName);
    }

    private Ast parse(CharStream charStream, String sourceName) {
        FluxusLexer lexer = new FluxusLexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);

        FluxusParser parser = new FluxusParser(commonTokenStream);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(new ErrorListener(sourceName));

        ParseTree cst = parser.compilationUnit();
        FluxusParserBaseVisitor<Node> listener = new Walker(sourceName);
        return (Ast) listener.visit(cst);
    }

}
