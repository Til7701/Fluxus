package de.til7701.fluxus.common.fons.util;

import de.til7701.fluxus.common.fons.*;
import de.til7701.fluxus.common.fons.Void;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.type.klass.Klass;

public class Fons {

    public static final Class<?>[] CLASSES = new Class<?>[]{
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

    public Klass[] getTypeDescriptors() {
        Klass[] klasses = new Klass[CLASSES.length];
        for (int i = 0; i < CLASSES.length; i++) {
            klasses[i] = getTypeDescriptor(CLASSES[i]);
        }
        return klasses;
    }

    private Klass getTypeDescriptor(Class<?> clazz) {
        try {
            return (Klass) ((LazyConstant<TypeDescriptor>) clazz.getDeclaredField("DESCRIPTOR").get(null)).get();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(clazz.toString(), e);
        }
    }

}
