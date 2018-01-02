package com.knobtviker.thermopile.di.modules.data;

import com.knobtviker.thermopile.data.sources.local.AtmosphereLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.HumidityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.PressureLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.TemperatureLocalDataSource;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bojan on 12/12/2017.
 */

@Module
public class AtmosphereDataModule {

    @Provides
    static AtmosphereLocalDataSource provideAtmosphereLocalDataSource() {
        return new AtmosphereLocalDataSource();
    }

    @Provides
    static TemperatureLocalDataSource provideTemperatureLocalDataSource() {
        return new TemperatureLocalDataSource();
    }

    @Provides
    static HumidityLocalDataSource provideHumidityLocalDataSource() {
        return new HumidityLocalDataSource();
    }

    @Provides
    static PressureLocalDataSource providePressureLocalDataSource() {
        return new PressureLocalDataSource();
    }
}
