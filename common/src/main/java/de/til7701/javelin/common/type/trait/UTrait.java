package de.til7701.javelin.common.type.trait;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.ast.type.trait.UType;
import de.til7701.javelin.common.environment.Environment;
import de.til7701.javelin.common.shaft.I;
import de.til7701.javelin.common.shaft.U;
import de.til7701.javelin.common.type.Metod;
import de.til7701.javelin.common.type.TypeDescriptor;
import de.til7701.javelin.common.type.klass.JavaKlass;
import de.til7701.javelin.common.type.klass.JavelinKlass;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class UTrait implements Trait {

    private final long bitCount;

    @Override
    public Optional<Metod> findMethod(String methodName, TypeDescriptor[] argumentTypes, Environment environment) {
        return switch (U.DESCRIPTOR.get()) {
            case JavaKlass klass -> klass.findMethod(methodName, argumentTypes, environment);
            case JavelinKlass _, Trait _ -> Optional.empty();
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
