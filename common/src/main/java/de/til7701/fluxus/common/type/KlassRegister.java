package de.til7701.fluxus.common.type;

import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.environment.Imports;
import de.til7701.fluxus.common.type.klass.Klass;
import de.til7701.fluxus.common.type.trait.CollectionTrait;
import de.til7701.fluxus.common.type.trait.FTrait;
import de.til7701.fluxus.common.type.trait.ITrait;
import de.til7701.fluxus.common.type.trait.UTrait;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class KlassRegister {

    private final Map<String, Klass> klasses = new HashMap<>();

    public void registerKlass(Klass klass) {
        klasses.put(klass.fullyQualifiedFluxusName(), klass);
    }

    public Optional<TypeDescriptor> find(Type type, Imports imports) {
        return switch (type) {
            case Type.Simple(_, String name) -> {
                Klass klass = klasses.get(imports.map(name));
                yield Optional.ofNullable(klass);
            }
            case Type.Trait.I(_, long bitCount) -> Optional.of(new ITrait(bitCount));
            case Type.Trait.U(_, long bitCount) -> Optional.of(new UTrait(bitCount));
            case Type.Trait.F(_, long bitCount) -> Optional.of(new FTrait(bitCount));
            case Type.Trait.Collection(_, Type indexType, Type elementType) -> find(elementType, imports).flatMap(e ->
                    find(indexType, imports).map(i ->
                            new CollectionTrait(e, i)
                    )
            );
        };
    }

}
