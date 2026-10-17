package de.til7701.javelin.common.type;

import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.common.environment.Environment;
import de.til7701.javelin.common.type.klass.Klass;
import de.til7701.javelin.common.type.trait.Trait;

import java.util.Optional;

public sealed interface TypeDescriptor permits Trait, Klass {

    Type type();

    Optional<Metod> findMethod(String methodName, TypeDescriptor[] argumentTypes, Environment environment);

}
