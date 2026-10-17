package de.til7701.javelin.ast.type;

import de.til7701.javelin.ast.Node;
import de.til7701.javelin.ast.type.trait.TraitType;

import java.util.function.Function;

public sealed interface Type extends Node permits SimpleType, GenericType, TraitType {

    Type mapNames(Function<String, String> nameMapper);

    String name();

}
