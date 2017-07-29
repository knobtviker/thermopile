package com.knobtviker.thermopile.domain.schedulers;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.schedulers.implementation.BaseSchedulerProvider;

import java.util.Optional;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Provides different types of schedulers.
 */
public class SchedulerProvider implements BaseSchedulerProvider {

    private static Optional<SchedulerProvider> INSTANCE = Optional.empty();

    private final Scheduler computation;
    private final Scheduler io;
    private final Scheduler sensors;
    private final Scheduler screensaver;
    private final Scheduler network;
    private final Scheduler ui;

    // Prevent direct instantiation.
    private SchedulerProvider() {
        this.computation = Schedulers.computation();
        this.io = Schedulers.io();
        this.sensors = Schedulers.newThread();
        this.screensaver = Schedulers.newThread();
        this.network = Schedulers.newThread();
        this.ui = AndroidSchedulers.mainThread();
    }

    public static SchedulerProvider getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new SchedulerProvider());
        }
        return INSTANCE.get();
    }

    @Override
    @NonNull
    public Scheduler computation() {
        return computation;
    }

    @Override
    @NonNull
    public Scheduler io() {
        return io;
    }

    @NonNull
    @Override
    public Scheduler screensaver() {
        return screensaver;
    }

    @Override
    @NonNull
    public Scheduler sensors() {
        return sensors;
    }

    @Override
    @NonNull
    public Scheduler network() {
        return network;
    }

    @Override
    @NonNull
    public Scheduler ui() {
        return ui;
    }
}
