package de.til7701.fluxus.common.type.trait;

import de.til7701.fluxus.common.type.TypeDescriptor;

public sealed interface Trait extends TypeDescriptor permits CollectionTrait, FTrait, ITrait, UTrait {

    boolean isSatisfied(TypeDescriptor typeDescriptor);

}
