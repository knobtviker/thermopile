package com.knobtviker.thermopile.di.qualifiers.presentation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by bojan on 17/08/2018.
 */

@Qualifier
@Documented
@Retention(RUNTIME)
public @interface ActivityScope {
}
