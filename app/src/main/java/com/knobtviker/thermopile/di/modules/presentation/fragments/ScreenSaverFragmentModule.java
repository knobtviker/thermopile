package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.ScreenSaverContract;
import com.knobtviker.thermopile.presentation.fragments.ScreenSaverFragment;
import com.knobtviker.thermopile.presentation.presenters.ScreenSaverPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ScreenSaverFragmentModule {

    @Provides
    ScreenSaverContract.Presenter providePresenter(
        @NonNull final ScreenSaverContract.View view,
        @NonNull final AtmosphereRepository atmosphereRepository,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final Schedulers schedulers
    ) {
        return new ScreenSaverPresenter(view, atmosphereRepository, settingsRepository, schedulers);
    }

    @Provides
    ScreenSaverContract.View provideView(@NonNull final ScreenSaverFragment fragment) {
        return fragment;
    }
}
