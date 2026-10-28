package de.til7701.fluxus.cli;

import de.til7701.fluxus.ast.Ast;
import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.TypeDefinition;
import de.til7701.fluxus.common.checker.Checker;
import de.til7701.fluxus.common.checker.Error;
import de.til7701.fluxus.common.environment.Environment;
import de.til7701.fluxus.common.flumen.Flumen;
import de.til7701.fluxus.common.type.klass.Klass;
import de.til7701.fluxus.common.util.Natives;
import de.til7701.fluxus.interpreter.Interpreter;
import de.til7701.fluxus.interpreter.InterpreterException;
import de.til7701.fluxus.parser.Parser;
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

    private final File baseDir;
    private final File entry;

    private final InputStream stdIn;
    private final PrintStream stdOut;

    public int run() throws IOException {
        Parser fileParser = new Parser();
        Ast ast = fileParser.parseFile(entry);

        final Environment env = new Environment(new Natives(stdIn, stdOut));
        Flumen flumen = new Flumen(env.getKlassLoader());
        for (Klass klass : flumen.getKlasses()) {
            env.getKlassRegister().registerKlass(klass);
        }

        String fullyQualifiedFluxusName = entry.getAbsolutePath()
                .replace(
                        baseDir.getAbsolutePath() + File.separator,
                        ""
                )
                .replace(File.separator, ".")
                .replace(".flx", "");
        if (ast instanceof TypeDefinition.ClassDefinition typeDefinition) {
            Klass klass = env.getKlassLoader().loadKlass(fullyQualifiedFluxusName, typeDefinition);
            env.getKlassRegister().registerKlass(klass);
        }

        Checker checker = new Checker(env);
        checker.check(ast, fullyQualifiedFluxusName);
        List<Error> errors = checker.getErrors();
        if (!errors.isEmpty()) {
            String code = Files.readString(entry.toPath());
            errors.forEach(e -> printError(code, e));
            return 1;
        }
        Interpreter interpreter = new Interpreter(env);
        try {
            interpreter.interpret(ast, fullyQualifiedFluxusName);
        } catch (InterpreterException e) {
            printInterpreterException(e);
        }
        return 0;
    }

    private void printError(String code, Error error) {
        stdOut.println(error);
        String spannedLines = extractSpanFullLines(code, error.span());
        stdOut.println(spannedLines);
    }

    private void printInterpreterException(InterpreterException e) {
        stdOut.println(e.getMessage());
        stdOut.println("at " + e.getSpan());
        e.getStack().print(stdOut);
        stdOut.println(e.getLiteralStore().valuesToString());
    }

    private String extractSpanFullLines(String code, Span span) {
        String[] lines = code.split("\n");
        int extractedLineCount = (span.endLine() + 1) - span.startLine();
        String[] extracted = new String[extractedLineCount];
        System.arraycopy(lines, span.startLine() - 1, extracted, 0, extractedLineCount);
        return String.join("\n", extracted);
    }

}
