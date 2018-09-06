package com.knobtviker.thermopile.di.modules.domain;

import android.os.Looper;

import com.knobtviker.thermopile.di.qualifiers.domain.SchedulerComputation;
import com.knobtviker.thermopile.di.qualifiers.domain.SchedulerIO;
import com.knobtviker.thermopile.di.qualifiers.domain.SchedulerNetwork;
import com.knobtviker.thermopile.di.qualifiers.domain.SchedulerScreensaver;
import com.knobtviker.thermopile.di.qualifiers.domain.SchedulerTrampoline;
import com.knobtviker.thermopile.di.qualifiers.domain.SchedulerUI;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by bojan on 12/12/2017.
 */

@Module
public class SchedulersModule {

    @Provides
    @Singleton
    @SchedulerComputation
    static Scheduler provideComputation() {
        return Schedulers.computation();
    }

    @Provides
    @Singleton
    @SchedulerIO
    static Scheduler provideIo() {
        return Schedulers.io();
    }

    @Provides
    @Singleton
    @SchedulerTrampoline
    static Scheduler provideTrampoline() {
        return Schedulers.trampoline();
    }

    @Provides
    @Singleton
    @SchedulerUI
    static Scheduler provideUi() {
        final Scheduler asyncMainThredScheduler = AndroidSchedulers.from(Looper.getMainLooper(), true);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(callable -> asyncMainThredScheduler);
        return asyncMainThredScheduler;
    }

    @Provides
    @Singleton
    @SchedulerNetwork
    static Scheduler provideNetwork() {
        return Schedulers.newThread();
    }

    @Provides
    @Singleton
    @SchedulerScreensaver
    static Scheduler provideScreensaver() {
        return Schedulers.single();
    }
}
