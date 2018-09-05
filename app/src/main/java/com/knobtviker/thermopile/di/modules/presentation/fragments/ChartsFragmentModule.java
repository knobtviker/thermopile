package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.presentation.Interval;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultChartType;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatDate;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatTime;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultMotion;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultMotionUnit;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultPressure;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultPressureUnit;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultTemperature;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultTemperatureUnit;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.ChartsContract;
import com.knobtviker.thermopile.presentation.fragments.ChartsFragment;
import com.knobtviker.thermopile.presentation.presenters.ChartsPresenter;
import com.knobtviker.thermopile.presentation.shared.constants.charts.ChartType;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitPressure;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;
import com.knobtviker.thermopile.presentation.views.adapters.ChartAdapter;

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
    @DefaultTemperatureUnit
    String provideDefaultTemperatureUnit(@NonNull final Context context) {
        return context.getString(R.string.unit_temperature_celsius);
    }

    @Provides
    @DefaultPressureUnit
    String provideDefaultPressureUnit(@NonNull final Context context) {
        return context.getString(R.string.unit_pressure_pascal);
    }

    @Provides
    @DefaultMotionUnit
    String provideDefaultMotionUnit(@NonNull final Context context) {
        return context.getString(R.string.unit_acceleration_ms2);
    }

    @Provides
    Interval providesDefaultInterval() {
        return DateTimeKit.today();
    }

    @Provides
    ChartAdapter providesAdapter() {
        return new ChartAdapter<>();
    }

    @Provides
    ChartsContract.Presenter providePresenter(
        @NonNull final ChartsContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final AtmosphereRepository atmosphereRepository,
        @NonNull final Schedulers schedulers
    ) {
        return new ChartsPresenter(view, settingsRepository, atmosphereRepository, schedulers);
    }

    @Provides
    ChartsContract.View provideView(@NonNull final ChartsFragment fragment) {
        return fragment;
    }
}
