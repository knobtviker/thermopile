package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatDate;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultTemperature;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.ScheduleContract;
import com.knobtviker.thermopile.presentation.fragments.ScheduleFragment;
import com.knobtviker.thermopile.presentation.presenters.SchedulePresenter;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;

import java.util.Collections;
import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
public class ScheduleFragmentModule {

    @Provides
    @DefaultFormatDate
    String provideDefaultFormatDate() {
        return FormatDate.EEEE_DD_MM_YYYY;
    }

    @Provides
    @DefaultTemperature
    int provideDefaultUnitTemperature() {
        return UnitTemperature.CELSIUS;
    }

    @Provides
    List<Threshold> provideThresholds() {
        return Collections.emptyList();
    }

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
