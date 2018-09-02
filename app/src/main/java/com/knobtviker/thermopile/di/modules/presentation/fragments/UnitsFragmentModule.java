package com.knobtviker.thermopile.di.modules.presentation.fragments;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultMotion;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultPressure;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultTemperature;
import com.knobtviker.thermopile.presentation.contracts.UnitsContract;
import com.knobtviker.thermopile.presentation.fragments.UnitsFragment;
import com.knobtviker.thermopile.presentation.presenters.UnitsPresenter;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitPressure;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;

import dagger.Module;
import dagger.Provides;

@Module
public class UnitsFragmentModule {

    @Provides
    long provideDefaultSettings() {
        return -1L;
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
    UnitsContract.Presenter providePresenter(@NonNull final UnitsContract.View view) {
        return new UnitsPresenter(view);
    }

    @Provides
    UnitsContract.View provideView(@NonNull final UnitsFragment fragment) {
        return fragment;
    }
}
