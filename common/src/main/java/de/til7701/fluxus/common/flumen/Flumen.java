package de.til7701.fluxus.common.flumen;

import de.til7701.fluxus.ast.Ast;
import de.til7701.fluxus.ast.TypeDefinition;
import de.til7701.fluxus.common.loading.KlassLoader;
import de.til7701.fluxus.common.type.klass.Klass;
import de.til7701.fluxus.parser.Parser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public final class Flumen {

    private static final String[] fdkClasses = {
            "fluxus.flumen.Str",
            "fluxus.flumen.System",
            "fluxus.flumen.UUID",
            "fluxus.flumen.Random",
            "fluxus.flumen.Math",
            "fluxus.flumen.lists.StrArrayList",
    };

    private static final String[] fdkTemplates = {
            "fluxus.flumen.templates.ArrayList",
    };

    @Getter
    private final List<Klass> klasses;
    @Getter
    private final List<Ast> asts = new ArrayList<>();

    private final KlassLoader klassLoader;

    public Flumen(KlassLoader klassLoader) {
        this.klassLoader = klassLoader;
        for (String t : fdkTemplates) {
            klassLoader.addTemplate(t, loadTemplate(t));
        }
        this.klasses = Arrays.stream(fdkClasses)
                .map(this::loadKlass)
                .toList();
    }

    private TypeDefinition.TemplateDefinition loadTemplate(String name) {
        Ast ast = loadAstFromResource(name);
        if (ast instanceof TypeDefinition.TemplateDefinition templateDefinition)
            return templateDefinition;
        else
            throw new RuntimeException("Expected TypeDefinition.TemplateDefinition: " + name);
    }

    private Klass loadKlass(String name) {
        Ast ast = loadAstFromResource(name);
        return switch (ast) {
            case TypeDefinition.ClassDefinition classDefinition -> klassLoader.loadKlass(name, classDefinition);
            case TypeDefinition.TemplateInstance templateInstance ->
                    klassLoader.instantiateTemplate(name, templateInstance);
            default -> throw new RuntimeException("Expected ClassDefinition or TemplateInstance: " + name);
        };
    }

    private Ast loadAstFromResource(String name) {
        String resourcePath = "/fdk/" + name.replace('.', '/') + ".flx";
        try (InputStream stream = Flumen.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new RuntimeException("FDK class not found: " + resourcePath);
            }
            Parser parser = new Parser();
            Ast ast = parser.parseInputStream(stream, resourcePath);
            asts.add(ast);
            return ast;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
