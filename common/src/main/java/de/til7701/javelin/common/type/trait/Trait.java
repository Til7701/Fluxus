package de.til7701.javelin.common.type.trait;

import de.til7701.javelin.common.type.TypeDescriptor;

public sealed interface Trait extends TypeDescriptor permits CollectionTrait, ITrait, UTrait {

    boolean isSatisfied(TypeDescriptor typeDescriptor);

}
