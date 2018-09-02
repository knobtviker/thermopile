package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultClockMode;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatTime;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultMaxWidth;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultTemperature;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultThreshold;
import com.knobtviker.thermopile.presentation.contracts.ThresholdContract;
import com.knobtviker.thermopile.presentation.fragments.ThresholdFragment;
import com.knobtviker.thermopile.presentation.presenters.ThresholdPresenter;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Default;
import com.knobtviker.thermopile.presentation.shared.constants.settings.ClockMode;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;

import dagger.Module;
import dagger.Provides;

@Module
public class ThresholdFragmentModule {

    @Provides
    @DefaultThreshold
    long provideDefaultThreshold() {
        return Default.INVALID_ID;
    }

    @Provides
    @DefaultMaxWidth
    int provideDefaultMaxWidth() {
        return Default.INVALID_WIDTH;
    }

    @Provides
    @DefaultClockMode
    int provideDefaultClockMode() {
        return ClockMode._24H;
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
    ThresholdContract.Presenter providePresenter(@NonNull final ThresholdContract.View view) {
        return new ThresholdPresenter(view);
    }

    @Provides
    ThresholdContract.View provideView(@NonNull final ThresholdFragment fragment) {
        return fragment;
    }
}
