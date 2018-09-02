package com.knobtviker.thermopile.di.modules.presentation.activities;

import android.support.annotation.NonNull;
import android.view.Window;

import com.knobtviker.thermopile.presentation.activities.ScreenSaverActivity;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class ScreenSaverActivityModule {

    @Provides
    static Window provideWindow(@NonNull final ScreenSaverActivity activity) {
        return activity.getWindow();
    }
}
