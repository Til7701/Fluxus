package de.til7701.fluxus.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.io.PrintStream;

@Getter
@RequiredArgsConstructor
public class Natives {

    private final InputStream stdIn;
    private final PrintStream stdOut;

}
