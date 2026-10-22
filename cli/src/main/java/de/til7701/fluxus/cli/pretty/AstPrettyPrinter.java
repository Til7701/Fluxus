package de.til7701.fluxus.cli.pretty;

import de.til7701.fluxus.ast.Ast;
import de.til7701.fluxus.ast.Script;
import de.til7701.fluxus.ast.statement.Statement;
import de.til7701.fluxus.ast.type_definition.classes.ClassDefinition;

import java.util.function.Consumer;

public class AstPrettyPrinter {

    private static final String INDENT = "    ";
    private static final String NEWLINE = System.lineSeparator();

    private final Consumer<String> outputConsumer;

    public AstPrettyPrinter(Consumer<String> outputConsumer) {
        this.outputConsumer = outputConsumer;
    }

    public void print(Ast ast, int indentLevel) {
        switch (ast) {
            case Script script -> {
                withIndent(indentLevel, "Script[" + NEWLINE);
                for (Statement statement : script.statements()) {
                    print(statement, indentLevel + 1);
                }
                withIndent(indentLevel, "]" + NEWLINE);
            }
            case ClassDefinition classDefinition -> {

            }
        }
    }

    private void print(Statement statement, int indentLevel) {

    }

    private void withIndent(int indentLevel, String text) {
        for (int i = 0; i < indentLevel; i++) {
            outputConsumer.accept(INDENT);
        }
        outputConsumer.accept(text);
    }

}
