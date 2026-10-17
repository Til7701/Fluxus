package de.til7701.javelin.ast.type.trait;

import de.til7701.javelin.ast.type.Type;

public sealed interface TraitType extends Type permits IType, UType, CollectionType {
}
