package de.til7701.fluxus.common.type.trait;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.type.SimpleType;
import de.til7701.fluxus.ast.type.Type;
import de.til7701.fluxus.ast.type.trait.CollectionType;
import de.til7701.fluxus.common.environment.Environment;
import de.til7701.fluxus.common.fons.Array;
import de.til7701.fluxus.common.fons.U;
import de.til7701.fluxus.common.type.Metod;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.type.klass.Klass;

import java.util.Optional;

public record CollectionTrait(TypeDescriptor elementType, TypeDescriptor indexType) implements Trait {

    @Override
    public Optional<Metod> findMethod(String methodName, TypeDescriptor[] argumentTypes, Environment environment) {
        return switch (methodName) {
            case "index" -> Optional.of(new TraitMetod(
                    true,
                    "index",
                    elementType.type(),
                    new Type[]{this.type(), indexType.type()}
            ));
            case "length" -> Optional.of(new TraitMetod(
                    false,
                    "length",
                    U.TYPE,
                    new Type[]{}
            ));
            case "as__fluxus_flumen_Str" -> Optional.of(new TraitMetod(
                    false,
                    "length",
                    new SimpleType(Span.undefined(), "fluxus.flumen.Str"),
                    new Type[]{this.type()}
            ));
            default -> Optional.empty();
        };
    }

    @Override
    public boolean isSatisfied(TypeDescriptor typeDescriptor) {
        return switch (typeDescriptor) {
            case Klass klass -> klass == Array.DESCRIPTOR.get() || klass.methods().stream().anyMatch(m ->
                    m.returnType().equals(elementType.type())
                            && m.parameterTypes().length == 2
                            && m.parameterTypes()[1].equals(indexType.type()));
            case Trait trait -> this.equals(trait);
        };
    }

    @Override
    public Type type() {
        return new CollectionType(Span.undefined(), indexType.type(), elementType.type());
    }

    @Override
    public String toString() {
        return elementType + "[" + indexType + "]";
    }
}
