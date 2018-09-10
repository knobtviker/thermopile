package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.UnitsContract;
import com.knobtviker.thermopile.presentation.fragments.UnitsFragment;
import com.knobtviker.thermopile.presentation.presenters.UnitsPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class UnitsFragmentModule {

    @Provides
    UnitsContract.Presenter providePresenter(
        @NonNull final UnitsContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final Schedulers schedulers
    ) {
        return new UnitsPresenter(view, settingsRepository, schedulers);
    }

    @Provides
    UnitsContract.View provideView(@NonNull final UnitsFragment fragment) {
        return fragment;
    }
}
