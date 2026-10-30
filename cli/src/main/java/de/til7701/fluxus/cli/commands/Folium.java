package de.til7701.fluxus.cli.commands;

import de.til7701.fluxus.cli.VersionProvider;
import de.til7701.fluxus.cli.mixins.DebugMixin;
import de.til7701.fluxus.common.flumen.Flumen;
import de.til7701.fluxus.common.folium.Doc;
import de.til7701.fluxus.common.folium.DocGenerator;
import de.til7701.fluxus.common.fons.util.Fons;
import de.til7701.fluxus.common.type.klass.Klass;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;

@Slf4j
@CommandLine.Command(
        name = "folium",
        mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class,
        description = "Generates markdown documentation for classes."
)
public class Folium implements Callable<Integer> {

    @CommandLine.Mixin
    private DebugMixin debugMixin;

    @CommandLine.Parameters(
            index = "0",
            description = "Folder to put the documents into.",
            arity = "1"
    )
    private File output;

    @Override
    public Integer call() throws Exception {
        validateArgs();
        DocGenerator generator = new DocGenerator();

        Fons fons = new Fons();
        for (Klass klass : fons.getTypeDescriptors()) {
            generateAndWriteKlass(generator, klass);
        }

        Flumen flumen = new Flumen();
        for (Klass klass : flumen.getKlasses()) {
            generateAndWriteKlass(generator, klass);
        }

        return 0;
    }

    private void validateArgs() {
        if (!output.isDirectory()) {
            throw new RuntimeException("Output is not a directory!");
        }
        if (!output.canWrite()) {
            throw new RuntimeException("Output is not writable!");
        }
    }

    private void generateAndWriteKlass(DocGenerator generator, Klass klass) throws IOException {
        Doc doc = generator.generate(klass);
        String relativePath = doc.fullyQualifiedFluxusName().replace('.', File.separatorChar) + ".md";
        File file = new File(output.getAbsolutePath() + File.separator + relativePath);
        boolean _ = file.getParentFile().mkdirs();
        if (!file.getParentFile().exists()) {
            throw new RuntimeException("Could not create parent folders!");
        }
        Files.writeString(file.toPath(), doc.content());
    }

}
