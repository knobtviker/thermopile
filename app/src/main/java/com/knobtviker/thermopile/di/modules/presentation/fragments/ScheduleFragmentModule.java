package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatDate;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultTemperature;
import com.knobtviker.thermopile.presentation.contracts.ScheduleContract;
import com.knobtviker.thermopile.presentation.fragments.ScheduleFragment;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;

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
    ScheduleContract.View provideView(@NonNull final ScheduleFragment fragment) {
        return fragment;
    }
}
