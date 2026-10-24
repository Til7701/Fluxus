package de.til7701.fluxus.common.fons.util;

import de.til7701.fluxus.common.fons.*;
import de.til7701.fluxus.common.fons.Void;
import de.til7701.fluxus.common.type.TypeDescriptor;

public sealed interface Primitive permits Array, Bool, Char, I, PType, U, Void {

    TypeDescriptor descriptor();

}
