package de.til7701.fluxus.common.environment;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Statement;
import de.til7701.fluxus.common.fons.Array;
import de.til7701.fluxus.common.fons.F;
import de.til7701.fluxus.common.fons.I;
import de.til7701.fluxus.common.fons.U;
import de.til7701.fluxus.common.fons.util.Fons;
import de.til7701.fluxus.common.type.KlassLoader;
import de.til7701.fluxus.common.type.KlassRegister;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.type.klass.Klass;
import de.til7701.fluxus.common.type.trait.*;
import de.til7701.fluxus.common.util.Natives;
import lombok.Getter;

@Getter
public final class Environment {

    private final KlassRegister klassRegister = new KlassRegister();
    private final KlassLoader klassLoader = new KlassLoader();

    private final Natives natives;
    private final Imports primitiveImports = new Imports();

    public Environment(Natives natives) {
        this.natives = natives;
        Fons fons = new Fons();
        for (Klass klass : fons.getTypeDescriptors()) {
            klassRegister.registerKlass(klass);
            primitiveImports.addImport(new Statement.Import(Span.undefined(), klass.fullyQualifiedFluxusName(), null));
        }
    }

    public Imports getPrimitiveImports() {
        return primitiveImports.snapshot();
    }

    public boolean isAssignable(TypeDescriptor from, TypeDescriptor to) {
        return switch (to) {
            case Klass toKlass -> switch (from) {
                case Klass fromKlass -> toKlass.fullyQualifiedFluxusName().equals(fromKlass.fullyQualifiedFluxusName());
                case ITrait _ -> toKlass.type().equals(I.TYPE);
                case UTrait _ -> toKlass.type().equals(U.TYPE);
                case FTrait _ -> toKlass.type().equals(F.TYPE);
                case CollectionTrait fromCollectionTrait ->
                        toKlass.type().equals(Array.TYPE) && isAssignable(fromCollectionTrait.indexType(), I.DESCRIPTOR.get());
            };
            case Trait toTrait -> toTrait.isSatisfied(from);
        };
    }

}
