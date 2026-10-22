package de.til7701.fluxus.cli;

import picocli.CommandLine;

public class ExceptionHandler implements CommandLine.IExecutionExceptionHandler {

    public int handleExecutionException(Exception ex, CommandLine cmd, CommandLine.ParseResult parseResult) {
        String message = ex.getMessage();
        cmd.getErr().println(cmd.getColorScheme().errorText("Error: " + (message != null ? message : ex.getClass().getName())));
        if (CommandLine.tracer().isDebug())
            ex.printStackTrace(cmd.getErr());

        return cmd.getExitCodeExceptionMapper() != null
                ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                : cmd.getCommandSpec().exitCodeOnExecutionException();
    }

}
