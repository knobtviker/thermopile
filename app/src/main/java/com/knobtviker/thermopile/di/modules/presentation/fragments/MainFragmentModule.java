package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.MainContract;
import com.knobtviker.thermopile.presentation.fragments.MainFragment;
import com.knobtviker.thermopile.presentation.presenters.MainPresenter;
import com.knobtviker.thermopile.presentation.utils.controllers.PIDController;
import com.knobtviker.thermopile.presentation.views.adapters.ThresholdAdapter;

import java.util.Arrays;

import dagger.Module;
import dagger.Provides;

@Module
public class MainFragmentModule {

    @Provides
    ThresholdAdapter provideThresholdAdapter(@NonNull final Context context) {
        return new ThresholdAdapter(
            Arrays.asList(context.getResources().getStringArray(R.array.weekdays)),
            Arrays.asList(context.getResources().getStringArray(R.array.weekdays_short))
        );
    }

    @Provides
    PIDController providePIDController() {
        return new PIDController(40, 1000);
    }

    @Provides
    MainContract.Presenter providePresenter(
        @NonNull final MainContract.View view,
        @NonNull final AtmosphereRepository atmosphereRepository,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final ThresholdRepository thresholdRepository,
        @NonNull final Schedulers schedulers
    ) {
        return new MainPresenter(view, atmosphereRepository, settingsRepository, thresholdRepository, schedulers);
    }

    @Provides
    MainContract.View provideView(@NonNull final MainFragment fragment) {
        return fragment;
    }
}
