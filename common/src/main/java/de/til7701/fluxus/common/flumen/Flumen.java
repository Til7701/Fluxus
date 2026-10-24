package de.til7701.fluxus.common.flumen;

import de.til7701.fluxus.ast.Ast;
import de.til7701.fluxus.ast.Script;
import de.til7701.fluxus.ast.type_definition.TypeDefinition;
import de.til7701.fluxus.common.type.KlassLoader;
import de.til7701.fluxus.common.type.klass.Klass;
import de.til7701.fluxus.parser.Parser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Flumen {

    private static final String[] sdkClassNames = {
            "fluxus.flumen.Str",
            "fluxus.flumen.System",
            "fluxus.flumen.UUID",
            "fluxus.flumen.Random",
    };

    @Getter
    private final List<Klass> klasses;
    @Getter
    private final List<Ast> asts = new ArrayList<>();

    private final KlassLoader klassLoader = new KlassLoader();

    public Flumen() {
        this.klasses = loadDefaultClasses();
    }

    private List<Klass> loadDefaultClasses() {
        return Arrays.stream(sdkClassNames)
                .map(this::loadClassFromResource)
                .toList();
    }

    private Klass loadClassFromResource(String name) {
        String resourcePath = "/fdk/" + name.replace('.', '/') + ".flx";
        try (InputStream stream = Flumen.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new RuntimeException("FDK class not found: " + resourcePath);
            }
            CharStream charStream = CharStreams.fromStream(stream);
            Parser parser = new Parser();
            Ast ast = parser.parse(charStream, resourcePath);
            asts.add(ast);
            return switch (ast) {
                case TypeDefinition typeDef -> klassLoader.loadKlassFromAst(name, typeDef);
                case Script _ ->
                        throw new RuntimeException("Expected TypeDefinition, got Script in Grip class: " + resourcePath);
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
