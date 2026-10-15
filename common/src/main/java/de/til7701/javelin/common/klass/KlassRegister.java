package de.til7701.javelin.common.klass;

import de.til7701.javelin.ast.type.IType;
import de.til7701.javelin.ast.type.SimpleType;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.ast.type.UType;
import de.til7701.javelin.common.NotImplementedException;
import de.til7701.javelin.common.environment.Imports;
import de.til7701.javelin.common.shaft.I;
import de.til7701.javelin.common.shaft.U;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KlassRegister {

    private final Map<String, Klass> klasses = new HashMap<>();

    public void registerKlass(Klass klass) {
        klasses.put(klass.fullyQualifiedJavelinName(), klass);
    }

    public Optional<Klass> getKlass(Type type, Imports imports) {
        return switch (type) {
            case SimpleType(_, String name) -> {
                Klass klass = klasses.get(imports.map(name));
                if (klass == null)
                    throw new KlassNotFoundException(type.toString());
                yield Optional.of(klass);
            }
            case IType _ -> Optional.of(klasses.get(I.TYPE.name()));
            case UType _ -> Optional.of(klasses.get(U.TYPE.name()));
            default -> throw new NotImplementedException(type.toString());
        };
    }

}
