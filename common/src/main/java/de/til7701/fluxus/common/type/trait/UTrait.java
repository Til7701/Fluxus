package de.til7701.fluxus.common.type.trait;

import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.type.Type;
import de.til7701.fluxus.ast.type.trait.UType;
import de.til7701.fluxus.common.environment.Environment;
import de.til7701.fluxus.common.fons.I;
import de.til7701.fluxus.common.fons.U;
import de.til7701.fluxus.common.type.Metod;
import de.til7701.fluxus.common.type.TypeDescriptor;
import de.til7701.fluxus.common.type.klass.FluxusKlass;
import de.til7701.fluxus.common.type.klass.JavaKlass;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@EqualsAndHashCode
@RequiredArgsConstructor
public final class UTrait implements Trait {

    private final long bitCount;

    @Override
    public Optional<Metod> findMethod(String methodName, TypeDescriptor[] argumentTypes, Environment environment) {
        return switch (U.DESCRIPTOR.get()) {
            case JavaKlass klass -> klass.findMethod(methodName, argumentTypes, environment);
            case FluxusKlass _, Trait _ -> Optional.empty();
        };
    }

    @Override
    public boolean isSatisfied(TypeDescriptor typeDescriptor) {
        return typeDescriptor == U.DESCRIPTOR.get() || typeDescriptor.type().equals(I.TYPE) || this.equals(typeDescriptor);
    }

    @Override
    public Type type() {
        return new UType(Span.undefined(), bitCount);
    }

    @Override
    public String toString() {
        return "U" + bitCount;
    }

}
