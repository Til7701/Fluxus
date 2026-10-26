package de.til7701.fluxus.common.type.trait;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.environment.Environment;
import de.til7701.fluxus.common.type.Metod;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.type.klass.FluxusKlass;
import de.til7701.fluxus.common.type.klass.JavaKlass;

import java.util.Optional;

public record ITrait(long bitCount) implements Trait {

    @Override
    public Optional<Metod> findMethod(String methodName, TypeDescriptor[] argumentTypes, Environment environment) {
        return switch (de.til7701.fluxus.common.fons.I.DESCRIPTOR.get()) {
            case JavaKlass klass -> klass.findMethod(methodName, argumentTypes, environment);
            case FluxusKlass _, Trait _ -> Optional.empty();
        };
    }

    @Override
    public boolean isSatisfied(TypeDescriptor typeDescriptor) {
        return typeDescriptor == de.til7701.fluxus.common.fons.I.DESCRIPTOR.get() || typeDescriptor.type().equals(de.til7701.fluxus.common.fons.I.TYPE) || this.equals(typeDescriptor);
    }

    @Override
    public Type type() {
        return new Type.Trait.I(Span.undefined(), bitCount);
    }

    @Override
    public String toString() {
        return "I" + bitCount;
    }

}
