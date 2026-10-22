package de.til7701.fluxus.common.fons;

import de.til7701.fluxus.common.type.TypeDescriptor;

public sealed interface Primitive permits Array, Bool, I, PType, Str, U, Void {

    TypeDescriptor descriptor();

}
