package com.knobtviker.thermopile.di.modules.presentation;

import com.knobtviker.thermopile.di.modules.presentation.activities.ScreenSaverActivityModule;
import com.knobtviker.thermopile.presentation.activities.MainActivity;
import com.knobtviker.thermopile.presentation.activities.ScreenSaverActivity;
import com.knobtviker.thermopile.presentation.activities.WirelessActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivitiesModule {

//    @ContributesAndroidInjector
//    abstract BaseActivity bindBaseActivity();

    @ContributesAndroidInjector
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = {ScreenSaverActivityModule.class})
    abstract ScreenSaverActivity bindScreenSaverActivity();

    @ContributesAndroidInjector
    abstract WirelessActivity bindWirelessActivity();
}