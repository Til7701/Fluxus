package de.til7701.javelin.common.shaft;

import de.til7701.javelin.common.type.TypeDescriptor;

public sealed interface Primitive permits Array, Bool, I, PType, Str, U, Void {

    TypeDescriptor descriptor();

}
