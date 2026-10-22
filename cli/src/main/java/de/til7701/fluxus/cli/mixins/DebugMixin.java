package de.til7701.fluxus.cli.mixins;

import picocli.CommandLine;

public final class DebugMixin {

    @CommandLine.Option(
            names = {"--debug"},
            description = "Enable debug output"
    )
    public void setDebug(boolean debug) {
        if (debug)
            CommandLine.tracer().setLevel(CommandLine.TraceLevel.DEBUG);
    }

}
