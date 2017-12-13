package com.knobtviker.thermopile.di.qualifiers.domain;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by bojan on 13/12/2017.
 */

@Qualifier
@Documented
@Retention(RUNTIME)
public @interface SchedulerComputation {
}
