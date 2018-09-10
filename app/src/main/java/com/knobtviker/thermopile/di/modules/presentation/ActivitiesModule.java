package com.knobtviker.thermopile.di.modules.presentation;

import com.knobtviker.thermopile.di.modules.presentation.activities.ScreenSaverActivityModule;
import com.knobtviker.thermopile.presentation.activities.MainActivity;
import com.knobtviker.thermopile.presentation.activities.ScreenSaverActivity;
import com.knobtviker.thermopile.presentation.activities.WirelessActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivitiesModule {

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector(modules = {ScreenSaverActivityModule.class})
    abstract ScreenSaverActivity contributeScreenSaverActivity();

    @ContributesAndroidInjector
    abstract WirelessActivity contributeWirelessActivity();
}