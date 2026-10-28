package de.til7701.fluxus.common.loading;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Statement;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.ast.TypeDefinition;
import de.til7701.fluxus.common.environment.Imports;
import de.til7701.fluxus.common.fons.Void;
import de.til7701.fluxus.common.type.Metod;
import de.til7701.fluxus.common.type.klass.Field;
import de.til7701.fluxus.common.type.klass.FluxusKlass;
import de.til7701.fluxus.common.type.klass.FluxusMetod;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TemplateInstantiator {

    public FluxusKlass instantiate(String klassName, TypeDefinition.TemplateDefinition template, TypeDefinition.TemplateInstance instance) {
        List<Statement.Import> imports = Stream.concat(
                        Stream.of(
                                new Statement.Import(Span.undefined(), klassName, null),
                                new Statement.Import(Span.undefined(), klassName, "This")
                        ),
                        Stream.concat(
                                template.imports().stream(),
                                instance.imports().stream()
                        )
                )
                .toList();

    }

}
