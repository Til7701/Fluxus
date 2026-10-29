package de.til7701.fluxus.common.fons.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FluxusDoc {

    String value();

}
