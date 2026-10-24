package de.til7701.fluxus.common.environment;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.statement.Import;
import de.til7701.fluxus.common.fons.*;
import de.til7701.fluxus.common.fons.Void;
import de.til7701.fluxus.common.fons.util.FluxusType;
import de.til7701.fluxus.common.type.KlassLoader;
import de.til7701.fluxus.common.type.KlassRegister;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.type.klass.Klass;
import de.til7701.fluxus.common.type.trait.*;
import de.til7701.fluxus.common.util.Natives;
import lombok.Getter;

@Getter
public class Environment {

    private static final Class<?>[] FONS = new Class<?>[]{
            Void.class,
            Bool.class,
            I.class,
            U.class,
            F.class,
            Char.class,
            Array.class,
            Native.class,
            PType.class,
    };

    private final KlassRegister klassRegister = new KlassRegister();
    private final KlassLoader klassLoader = new KlassLoader();

    private final Natives natives;
    private final Imports primitiveImports = new Imports();

    public Environment(Natives natives) {
        this.natives = natives;
        for (Class<?> javaClass : FONS) {
            this.addJavaClass(javaClass);
            primitiveImports.addImport(new Import(Span.undefined(), getFullyQualifiedFluxusName(javaClass), null));
        }
    }

    private void addJavaClass(Class<?> clazz) {
        try {
            Klass descriptor = (Klass) ((LazyConstant<TypeDescriptor>) clazz.getDeclaredField("DESCRIPTOR").get(null)).get();
            klassRegister.registerKlass(descriptor);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(clazz.toString(), e);
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
                        toKlass.type().equals(Array.TYPE) && isAssignable(fromCollectionTrait.getIndexType(), I.DESCRIPTOR.get());
            };
            case Trait toTrait -> toTrait.isSatisfied(from);
        };
    }

    private String getFullyQualifiedFluxusName(Class<?> clazz) {
        return clazz.getAnnotation(FluxusType.class).fullyQualifiedFluxusName();
    }

}
