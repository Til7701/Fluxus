package de.til7701.fluxus.common.type.trait;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.type.Type;
import de.til7701.fluxus.ast.type.trait.FType;
import de.til7701.fluxus.common.environment.Environment;
import de.til7701.fluxus.common.fons.F;
import de.til7701.fluxus.common.type.Metod;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.type.klass.FluxusKlass;
import de.til7701.fluxus.common.type.klass.JavaKlass;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@EqualsAndHashCode
@RequiredArgsConstructor
public final class FTrait implements Trait {

    private final long bitCount;

    @Override
    public Optional<Metod> findMethod(String methodName, TypeDescriptor[] argumentTypes, Environment environment) {
        return switch (F.DESCRIPTOR.get()) {
            case JavaKlass klass -> klass.findMethod(methodName, argumentTypes, environment);
            case FluxusKlass _, Trait _ -> Optional.empty();
        };
    }

    @Override
    public boolean isSatisfied(TypeDescriptor typeDescriptor) {
        return typeDescriptor == F.DESCRIPTOR.get() || typeDescriptor.type().equals(F.TYPE) || this.equals(typeDescriptor);
    }

    @Override
    public Type type() {
        return new FType(Span.undefined(), bitCount);
    }

    @Override
    public String toString() {
        return "U" + bitCount;
    }

}
