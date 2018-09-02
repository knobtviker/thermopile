package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultClockMode;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatDate;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatTime;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultMotion;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultPressure;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultTemperature;
import com.knobtviker.thermopile.presentation.contracts.MainContract;
import com.knobtviker.thermopile.presentation.fragments.MainFragment;
import com.knobtviker.thermopile.presentation.presenters.MainPresenter;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Default;
import com.knobtviker.thermopile.presentation.shared.constants.settings.ClockMode;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitPressure;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;
import com.knobtviker.thermopile.presentation.utils.controllers.PIDController;

import org.threeten.bp.ZoneId;

import dagger.Module;
import dagger.Provides;

@Module
public class MainFragmentModule {

    @Provides
    ZoneId provideDefaultZoneId() {
        return DateTimeKit.zoneById(Default.TIMEZONE);
    }

    @Provides
    @DefaultClockMode
    int provideDefaultClockMode() {
        return ClockMode._24H;
    }

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
    PIDController providePIDController() {
        return new PIDController(40, 1000);
    }

    @Provides
    MainContract.Presenter providePresenter(@NonNull final MainContract.View view) {
        return new MainPresenter(view);
    }

    @Provides
    MainContract.View provideView(@NonNull final MainFragment fragment) {
        return fragment;
    }
}
