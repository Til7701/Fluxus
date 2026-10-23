package de.til7701.fluxus.test.cli;

import de.til7701.fluxus.cli.Runner;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ScriptTest {

    @ParameterizedTest(name = "Script {0}")
    @MethodSource("findScriptFiles")
    public void testScripts(File file) throws IOException {
        InputStream in = new BufferedInputStream(new ByteArrayInputStream(new byte[0]));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Runner runner = new Runner(
                file,
                in,
                new PrintStream(out)
        );
        int exitCode = runner.run();
        String output = out.toString();
        verifyOutput(file, output);

        if (exitCode != 0) fail("Exit code is: " + exitCode);
    }

    private static Stream<File> findScriptFiles() throws URISyntaxException, IOException {
        URL folderUrl = ScriptTest.class.getResource("/scripts");
        assertNotNull(folderUrl);
        return Files.find(Path.of(folderUrl.toURI()), 10,
                        ((path, _) -> path.toString().endsWith(".flx")))
                .map(Path::toFile);
    }

    private static void verifyOutput(File source, String actualOutput) throws IOException {
        File expectedOutputFile = new File(source.getAbsolutePath().replace(".flx", ".txt"));
        String expectedOutput = Files.readString(expectedOutputFile.toPath());
        assertEquals(expectedOutput, actualOutput);
    }

}
