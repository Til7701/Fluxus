package de.til7701.fluxus.interpreter;

import de.til7701.fluxus.ast.Span;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@AllArgsConstructor
@RequiredArgsConstructor
public final class ReturnException extends RuntimeException {

    @Getter
    @Setter
    private @Nullable Span returnSpan;
    @Getter
    private final Variable returnValue;

}
