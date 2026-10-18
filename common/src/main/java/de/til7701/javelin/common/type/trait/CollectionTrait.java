package de.til7701.javelin.common.type.trait;

import de.til7701.javelin.ast.Span;
import de.til7701.javelin.ast.type.Type;
import de.til7701.javelin.ast.type.trait.CollectionType;
import de.til7701.javelin.common.environment.Environment;
import de.til7701.javelin.common.type.Metod;
import de.til7701.javelin.common.type.TypeDescriptor;
import de.til7701.javelin.common.type.klass.Klass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class CollectionTrait implements Trait {

    private final TypeDescriptor elementType;
    private final TypeDescriptor indexType;

    @Override
    public Optional<Metod> findMethod(String methodName, TypeDescriptor[] argumentTypes, Environment environment) {
        return Optional.of(new TraitMetod(
                true,
                "index",
                elementType.type(),
                new Type[]{this.type(), indexType.type()}
        ));
    }

    @Override
    public boolean isSatisfied(TypeDescriptor typeDescriptor) {
        return switch (typeDescriptor) {
            case Klass klass -> klass.methods().stream().anyMatch(m ->
                    m.returnType().equals(elementType.type())
                            && m.parameterTypes().length == 2
                            && m.parameterTypes()[1].equals(indexType.type()));
            case Trait trait -> this.equals(trait);
        };
    }

    @Override
    public Type type() {
        return new CollectionType(Span.undefined(), indexType.type(), elementType.type());
    }

    @Override
    public String toString() {
        return elementType + "[" + indexType + "]";
    }
}
