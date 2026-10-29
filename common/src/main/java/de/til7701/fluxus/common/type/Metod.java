package de.til7701.fluxus.common.type;

import de.til7701.fluxus.ast.DocComment;
import de.til7701.fluxus.ast.Span;
import de.til7701.fluxus.ast.Type;
import de.til7701.fluxus.common.type.klass.FluxusMetod;
import de.til7701.fluxus.common.type.klass.JavaMetod;
import de.til7701.fluxus.common.type.trait.TraitMetod;
import org.jspecify.annotations.Nullable;

public sealed interface Metod permits JavaMetod, FluxusMetod, TraitMetod {

    Span span();

    @Nullable DocComment docComment();

    boolean isStatic();

    String name();

    Type returnType();

    Type[] parameterTypes();

}
