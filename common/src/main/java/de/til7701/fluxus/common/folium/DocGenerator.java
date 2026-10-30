package de.til7701.fluxus.common.folium;

import de.til7701.fluxus.ast.DocComment;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.fons.Void;
import de.til7701.fluxus.common.type.Metod;
import de.til7701.fluxus.common.type.klass.Field;
import de.til7701.fluxus.common.type.klass.FluxusKlass;
import de.til7701.fluxus.common.type.klass.FluxusMetod;
import de.til7701.fluxus.common.type.klass.Klass;

import java.util.Collection;
import java.util.Comparator;

public class DocGenerator {

    private static final String VERSION = System.getProperty("fluxus.version");

    public Collection<Doc> generate(Collection<Klass> klasses) {
        return klasses.stream()
                .map(this::generate)
                .toList();
    }

    public Doc generate(Klass klass) {
        StringBuilder sb = new StringBuilder();

        sb.append("<p align=\"right\">Fluxus: ")
                .append(VERSION)
                .append("</p>\n\n");

        DocComment docComment = klass.docComment();
        if (docComment != null)
            sb.append(docComment.comment());
        sb.append("\n");
        appendFields(sb, klass);
        sb.append("\n");
        appendConstructors(sb, klass);
        sb.append("\n");
        appendMethods(sb, klass);

        return new Doc(
                klass.fullyQualifiedFluxusName(),
                sb.toString()
        );
    }

    private void appendFields(StringBuilder sb, Klass klass) {
        if (klass instanceof FluxusKlass fluxusKlass) {
            sb.append("## Fields\n\n");
            sb.append("### Static Fields\n\n");
            fluxusKlass.fields().stream()
                    .filter(Field::isStatic)
                    .forEach(f -> appendField(sb, f));
            sb.append("### Instance Fields\n\n");
            fluxusKlass.fields().stream()
                    .filter(field -> !field.isStatic())
                    .forEach(f -> appendField(sb, f));
        }
    }

    private void appendField(StringBuilder sb, Field field) {
        sb.append("#### `");
        sb.append(field.type().toCode());
        sb.append(" ");
        sb.append(field.name());
        sb.append(" = ");
        sb.append(field.init().toSingleLineCode());
        sb.append("`\n\n");
        DocComment docComment = field.docComment();
        if (docComment != null) sb.append(docComment.comment());
        else sb.append("*No description provided.*");
        sb.append("\n\n");
    }

    private void appendConstructors(StringBuilder sb, Klass klass) {
        if (klass instanceof FluxusKlass fluxusKlass) {
            sb.append("## Constructors\n\n");
            fluxusKlass.constructors()
                    .forEach(f -> appendConstructor(sb, f));
        }
    }

    private void appendConstructor(StringBuilder sb, FluxusMetod constructor) {
        sb.append("#### `(");
        String[] parameterNames = constructor.parameterNames();
        Type[] parameterTypes = constructor.parameterTypes();
        for (int i = 0; i < constructor.parameterNames().length - 1; i++) {
            sb.append(parameterTypes[i].toCode());
            sb.append(" ");
            sb.append(parameterNames[i]);
            sb.append(", ");
        }
        if (parameterNames.length > 0) {
            sb.append(parameterTypes[parameterTypes.length - 1].toCode());
            sb.append(" ");
            sb.append(parameterNames[parameterNames.length - 1]);
        }
        sb.append(")`\n\n");
        DocComment docComment = constructor.docComment();
        if (docComment != null) sb.append(docComment.comment());
        else sb.append("*No description provided.*");
        sb.append("\n\n");
    }

    private void appendMethods(StringBuilder sb, Klass klass) {
        sb.append("## Methods\n\n");
        sb.append("### Static Methods\n\n");
        klass.methods().stream()
                .filter(Metod::isStatic)
                .sorted(Comparator.comparing(Metod::name))
                .forEach(m -> appendMethod(sb, m));
        sb.append("### Instance Methods\n\n");
        klass.methods().stream()
                .filter(metod -> !metod.isStatic())
                .sorted(Comparator.comparing(Metod::name))
                .forEach(m -> appendMethod(sb, m));
    }

    private void appendMethod(StringBuilder sb, Metod metod) {
        sb.append("#### `");
        Type returnType = metod.returnType();
        if (!returnType.equals(Void.TYPE))
            sb.append(returnType.toCode()).append(" ");
        sb.append(metod.name());
        sb.append("(");
        String[] parameterNames = metod.parameterNames();
        Type[] parameterTypes = metod.parameterTypes();
        for (int i = 0; i < metod.parameterNames().length - 1; i++) {
            sb.append(parameterTypes[i].toCode());
            sb.append(" ");
            sb.append(parameterNames[i]);
            sb.append(", ");
        }
        if (parameterNames.length > 0) {
            sb.append(parameterTypes[parameterTypes.length - 1].toCode());
            sb.append(" ");
            sb.append(parameterNames[parameterNames.length - 1]);
        }
        sb.append(")`\n\n");
        DocComment docComment = metod.docComment();
        if (docComment != null) sb.append(docComment.comment());
        else sb.append("*No description provided.*");
        sb.append("\n\n");
    }

}
