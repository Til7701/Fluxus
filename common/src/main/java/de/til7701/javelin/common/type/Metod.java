package de.til7701.javelin.common.type;

import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.common.type.klass.JavaMetod;
import de.til7701.javelin.common.type.klass.JavelinMetod;
import de.til7701.javelin.common.type.trait.TraitMetod;

public sealed interface Metod permits JavaMetod, JavelinMetod, TraitMetod {

    boolean isStatic();

    String name();

    Type returnType();

    Type[] parameterTypes();

}
