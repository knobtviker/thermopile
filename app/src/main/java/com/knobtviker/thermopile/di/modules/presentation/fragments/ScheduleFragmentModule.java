package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.ScheduleContract;
import com.knobtviker.thermopile.presentation.fragments.ScheduleFragment;
import com.knobtviker.thermopile.presentation.presenters.SchedulePresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ScheduleFragmentModule {

    @Provides
    ScheduleContract.Presenter providePresenter(
        @NonNull final ScheduleContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final ThresholdRepository thresholdRepository,
        @NonNull final Schedulers schedulers
    ) {
        return new SchedulePresenter(view, settingsRepository, thresholdRepository, schedulers);
    }

    @Provides
    ScheduleContract.View provideView(@NonNull final ScheduleFragment fragment) {
        return fragment;
    }
}
