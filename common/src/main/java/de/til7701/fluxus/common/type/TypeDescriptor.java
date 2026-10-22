package de.til7701.fluxus.common.type;

import de.til7701.fluxus.ast.type.Type;
import de.til7701.fluxus.common.environment.Environment;
import de.til7701.fluxus.common.type.klass.Klass;
import de.til7701.fluxus.common.type.trait.Trait;

import java.util.Optional;

public sealed interface TypeDescriptor permits Trait, Klass {

    Type type();

    Optional<Metod> findMethod(String methodName, TypeDescriptor[] argumentTypes, Environment environment);

}
