package de.til7701.fluxus.common.type;

import de.til7701.fluxus.ast.type.GenericType;
import de.til7701.fluxus.ast.type.SimpleType;
import de.til7701.fluxus.ast.type.Type;
import de.til7701.fluxus.ast.type.trait.CollectionType;
import de.til7701.fluxus.ast.type.trait.FType;
import de.til7701.fluxus.ast.type.trait.IType;
import de.til7701.fluxus.ast.type.trait.UType;
import de.til7701.fluxus.common.NotImplementedException;
import de.til7701.fluxus.common.environment.Imports;
import de.til7701.fluxus.common.type.klass.Klass;
import de.til7701.fluxus.common.type.trait.CollectionTrait;
import de.til7701.fluxus.common.type.trait.FTrait;
import de.til7701.fluxus.common.type.trait.ITrait;
import de.til7701.fluxus.common.type.trait.UTrait;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KlassRegister {

    private final Map<String, Klass> klasses = new HashMap<>();

    public void registerKlass(Klass klass) {
        klasses.put(klass.fullyQualifiedFluxusName(), klass);
    }

    public Optional<TypeDescriptor> find(Type type, Imports imports) {
        return switch (type) {
            case SimpleType(_, String name) -> {
                Klass klass = klasses.get(imports.map(name));
                if (klass == null)
                    throw new KlassNotFoundException(type.toString());
                yield Optional.of(klass);
            }
            case GenericType _ -> throw new NotImplementedException();
            case IType(_, long bitCount) -> Optional.of(new ITrait(bitCount));
            case UType(_, long bitCount) -> Optional.of(new UTrait(bitCount));
            case FType(_, long bitCount) -> Optional.of(new FTrait(bitCount));
            case CollectionType(_, Type indexType, Type elementType) -> find(elementType, imports).flatMap(e ->
                    find(indexType, imports).map(i ->
                            new CollectionTrait(e, i)
                    )
            );
        };
    }

}
