package de.til7701.fluxus.cli.commands;

import de.til7701.fluxus.cli.Runner;
import de.til7701.fluxus.cli.VersionProvider;
import de.til7701.fluxus.cli.mixins.DebugMixin;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@Slf4j
@CommandLine.Command(
        name = "fluxus",
        mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class,
        description = "The Fluxus programming language."
)
public class Fluxus implements Callable<Integer> {

    @CommandLine.Mixin
    private DebugMixin debugMixin;

    @CommandLine.Parameters(
            index = "0",
            description = "The source file to execute.",
            arity = "1"
    )
    private File source;

    @Override
    public Integer call() throws Exception {
        Runner runner = new Runner(
                new File("."),
                source,
                System.in,
                System.out
        );
        return runner.run();
    }

}
