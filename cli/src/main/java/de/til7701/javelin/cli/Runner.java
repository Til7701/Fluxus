package de.til7701.javelin.cli;

import de.til7701.javelin.ast.Ast;
import de.til7701.javelin.ast.Span;
import de.til7701.javelin.checker.Checker;
import de.til7701.javelin.checker.Error;
import de.til7701.javelin.cli.pretty.AstPrettyPrinter;
import de.til7701.javelin.common.environment.Environment;
import de.til7701.javelin.common.head.Head;
import de.til7701.javelin.common.klass.Klass;
import de.til7701.javelin.common.util.Natives;
import de.til7701.javelin.interpreter.Interpreter;
import de.til7701.javelin.parser.Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class Runner {

    private final File entry;

    private final InputStream stdIn;
    private final PrintStream stdOut;

    public int run() throws IOException {
        Parser fileParser = new Parser();
        Ast ast = fileParser.parseFile(entry);

        if (log.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            AstPrettyPrinter prettyPrinter = new AstPrettyPrinter(builder::append);
            prettyPrinter.print(ast, 0);
            log.debug("Parsed AST:\n{}", builder);
        }

        final Environment env = new Environment(new Natives(stdIn, stdOut));
        Head head = new Head();
        if (log.isDebugEnabled()) {
            List<Ast> asts = head.getAsts();
            for (int i = 0; i < asts.size(); i++) {
                StringBuilder builder = new StringBuilder();
                AstPrettyPrinter prettyPrinter = new AstPrettyPrinter(builder::append);
                prettyPrinter.print(asts.get(i), 0);
                log.debug("Parsed Grip class '{}':\n{}", head.getKlasses().get(i).fullyQualifiedJavelinName(), builder);
            }
        }
        for (Klass klass : head.getKlasses()) {
            env.getKlassRegister().registerKlass(klass);
        }
        Checker checker = new Checker(env);
        checker.check(ast);
        List<Error> errors = checker.getErrors();
        if (!errors.isEmpty()) {
            String code = Files.readString(entry.toPath());
            errors.forEach(e -> printError(code, e));
            return 1;
        }
        Interpreter interpreter = new Interpreter(env);
        interpreter.interpret(ast);
        return 0;
    }

    private void printError(String code, Error error) {
        stdOut.println(error);
        String spannedLines = extractSpanFullLines(code, error.span());
        stdOut.println(spannedLines);
    }

    private String extractSpanFullLines(String code, Span span) {
        String[] lines = code.split("\n");
        int extractedLineCount = (span.endLine() + 1) - span.startLine();
        String[] extracted = new String[extractedLineCount];
        System.arraycopy(lines, span.startLine() - 1, extracted, 0, extractedLineCount);
        return String.join("\n", extracted);
    }

}
