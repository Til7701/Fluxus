package de.til7701.javelin.common.environment;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.statement.Import;
import de.til7701.javelin.common.shaft.*;
import de.til7701.javelin.common.shaft.System;
import de.til7701.javelin.common.shaft.Void;
import de.til7701.javelin.common.type.KlassLoader;
import de.til7701.javelin.common.type.KlassRegister;
import de.til7701.javelin.common.type.TypeDescriptor;
import de.til7701.javelin.common.type.klass.Klass;
import de.til7701.javelin.common.type.trait.CollectionTrait;
import de.til7701.javelin.common.type.trait.ITrait;
import de.til7701.javelin.common.type.trait.Trait;
import de.til7701.javelin.common.type.trait.UTrait;
import de.til7701.javelin.common.util.Natives;
import lombok.Getter;

@Getter
public class Environment {

    private static final Class<?>[] SHAFT = new Class<?>[]{
            Void.class,
            Bool.class,
            I.class,
            U.class,
            Str.class,
            Array.class,
            System.class,
    };

    private final KlassRegister klassRegister = new KlassRegister();
    private final KlassLoader klassLoader = new KlassLoader();

    private final Natives natives;
    private final Imports primitiveImports = new Imports();

    public Environment(Natives natives) {
        this.natives = natives;
        for (Class<?> javaClass : SHAFT) {
            this.addJavaClass(javaClass);
            primitiveImports.addImport(new Import(Span.undefined(), getFullyQualifiedJavelinName(javaClass), null));
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
                case Klass fromKlass ->
                        toKlass.fullyQualifiedJavelinName().equals(fromKlass.fullyQualifiedJavelinName());
                case ITrait _ -> toKlass.type().equals(I.TYPE);
                case UTrait _ -> toKlass.type().equals(U.TYPE);
                case CollectionTrait fromCollectionTrait ->
                        toKlass.type().equals(Array.TYPE) && isAssignable(fromCollectionTrait.getIndexType(), I.DESCRIPTOR.get());
            };
            case Trait toTrait -> toTrait.isSatisfied(from);
        };
    }

    private String getFullyQualifiedJavelinName(Class<?> clazz) {
        return clazz.getAnnotation(JavelinType.class).fullyQualifiedJavelinName();
    }

}
