package de.til7701.fluxus.interpreter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReturnException extends RuntimeException {

    @Getter
    private final Variable returnValue;

}
