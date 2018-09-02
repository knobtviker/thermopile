package com.knobtviker.thermopile.di.qualifiers.presentation.defaults;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by bojan on 01/09/2018.
 */

@Qualifier
@Documented
@Retention(RUNTIME)
public @interface DefaultTemperature {
}
