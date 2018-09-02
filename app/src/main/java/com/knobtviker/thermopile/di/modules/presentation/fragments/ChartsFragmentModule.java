package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultChartType;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatDate;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatTime;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultMotion;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultPressure;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultTemperature;
import com.knobtviker.thermopile.presentation.contracts.ChartsContract;
import com.knobtviker.thermopile.presentation.fragments.ChartsFragment;
import com.knobtviker.thermopile.presentation.shared.constants.charts.ChartType;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitPressure;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;

import dagger.Module;
import dagger.Provides;

@Module
public class ChartsFragmentModule {

    @Provides
    @DefaultFormatDate
    String provideDefaultFormatDate() {
        return FormatDate.EEEE_DD_MM_YYYY;
    }

    @Provides
    @DefaultFormatTime
    String provideDefaultFormatTime() {
        return FormatTime.HH_MM;
    }

    @Provides
    @DefaultTemperature
    int provideDefaultUnitTemperature() {
        return UnitTemperature.CELSIUS;
    }

    @Provides
    @DefaultPressure
    int provideDefaultUnitPressure() {
        return UnitPressure.PASCAL;
    }

    @Provides
    @DefaultMotion
    int provideDefaultUnitMotion() {
        return UnitAcceleration.METERS_PER_SECOND_2;
    }

    @Provides
    @DefaultChartType
    int provideDefaultChartType() {
        return ChartType.TEMPERATURE;
    }

    @Provides
    ChartsContract.View provideView(@NonNull final ChartsFragment fragment) {
        return fragment;
    }
}
