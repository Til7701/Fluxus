package de.til7701.fluxus.cli;

import picocli.CommandLine;

class Main {

    static void main(Object command, String[] args) {
        CommandLine.Help.ColorScheme colorScheme = new CommandLine.Help.ColorScheme.Builder()
                .commands(CommandLine.Help.Ansi.Style.bold, CommandLine.Help.Ansi.Style.underline)
                .options(CommandLine.Help.Ansi.Style.fg_cyan)
                .parameters(CommandLine.Help.Ansi.Style.fg_cyan)
                .optionParams(CommandLine.Help.Ansi.Style.italic)
                .errors(CommandLine.Help.Ansi.Style.fg_red, CommandLine.Help.Ansi.Style.bold)
                .stackTraces(CommandLine.Help.Ansi.Style.italic)
                .applySystemProperties() // optional: allow end users to customize
                .ansi(CommandLine.Help.Ansi.ON)
                .build();
        CommandLine cli = new CommandLine(command);
        cli.setExecutionExceptionHandler(new ExceptionHandler());
        cli.setExitCodeExceptionMapper(new ExceptionExitCodeMapper());
        cli.setColorScheme(colorScheme);
        int exitCode = cli.execute(args);
        System.exit(exitCode);
    }

}
