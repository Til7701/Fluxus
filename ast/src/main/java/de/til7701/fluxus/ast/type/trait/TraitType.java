package de.til7701.fluxus.ast.type.trait;

import de.til7701.fluxus.ast.type.Type;

public sealed interface TraitType extends Type permits CollectionType, FType, IType, UType {
}
