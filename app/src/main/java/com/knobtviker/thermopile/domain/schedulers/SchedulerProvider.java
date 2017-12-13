package com.knobtviker.thermopile.domain.schedulers;

import com.knobtviker.thermopile.di.qualifiers.domain.SchedulerComputation;
import com.knobtviker.thermopile.di.qualifiers.domain.SchedulerIO;
import com.knobtviker.thermopile.di.qualifiers.domain.SchedulerNetwork;
import com.knobtviker.thermopile.di.qualifiers.domain.SchedulerScreensaver;
import com.knobtviker.thermopile.di.qualifiers.domain.SchedulerUI;

import javax.inject.Inject;

import io.reactivex.Scheduler;


public class SchedulerProvider  {

    @Inject
    @SchedulerComputation
    public Scheduler computation;

    @Inject
    @SchedulerIO
    public Scheduler io;

    @Inject
    @SchedulerScreensaver
    public Scheduler screensaver;

    @Inject
    @SchedulerNetwork
    public Scheduler network;

    @Inject
    @SchedulerUI
    public Scheduler ui;

    @Inject
    public SchedulerProvider() {
    }
}
