package de.til7701.fluxus.ast.type;

import de.til7701.fluxus.ast.Node;
import de.til7701.fluxus.ast.type.trait.TraitType;

import java.util.function.Function;

public sealed interface Type extends Node permits SimpleType, GenericType, TraitType {

    Type mapNames(Function<String, String> nameMapper);

    String name();

}
