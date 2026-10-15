package de.til7701.javelin.common.environment;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.statement.Import;
import de.til7701.javelin.common.klass.KlassLoader;
import de.til7701.javelin.common.klass.KlassRegister;
import de.til7701.javelin.common.shaft.*;
import de.til7701.javelin.common.shaft.System;
import de.til7701.javelin.common.shaft.Void;
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

    public void addJavaClass(Class<?> clazz) {
        klassRegister.registerKlass(klassLoader.loadJavaClass(clazz));
    }

    public Imports getPrimitiveImports() {
        return primitiveImports.snapshot();
    }

    private String getFullyQualifiedJavelinName(Class<?> clazz) {
        return clazz.getAnnotation(JavelinType.class).fullyQualifiedJavelinName();
    }

}
