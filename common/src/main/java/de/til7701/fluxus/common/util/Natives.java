package de.til7701.fluxus.common.util;

import java.io.InputStream;
import java.io.PrintStream;

public record Natives(InputStream stdIn, PrintStream stdOut) {
}
