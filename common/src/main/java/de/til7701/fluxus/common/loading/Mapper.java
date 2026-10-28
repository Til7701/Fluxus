package de.til7701.fluxus.common.loading;

import de.til7701.fluxus.ast.Expression;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.ast.TypeDefinition;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

class Mapper {

    private final Map<String, Expression> expressionMap = new HashMap<>();
    private final Map<String, Type> typeMap = new HashMap<>();

    Mapper(TypeDefinition.TemplateDefinition template) {
        template.templateVariables().forEach(v -> {
            switch (v) {
                case TypeDefinition.TemplateVariable.Expression expression ->
                    expressionMap.put( expression.expression());
                case TypeDefinition.TemplateVariable.Type type -> {
                }
            }
        });
    }

    @Nullable Expression mapToExpression(String identifier) {
        return expressionMap.get(identifier);
    }

    @Nullable Type mapToType(String identifier) {
        return typeMap.get(identifier);
    }

}
