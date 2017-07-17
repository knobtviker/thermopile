package com.knobtviker.thermopile.domain.schedulers.implementation;

import android.support.annotation.NonNull;

import io.reactivex.Scheduler;


/**
 * Allow providing different types of {@link Scheduler}s.
 */
public interface BaseSchedulerProvider {

    @NonNull
    Scheduler computation();

    @NonNull
    Scheduler io();

    @NonNull
    Scheduler sensors();

    @NonNull
    Scheduler network();

    @NonNull
    Scheduler ui();
}
