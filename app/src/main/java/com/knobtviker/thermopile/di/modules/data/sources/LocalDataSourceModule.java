package com.knobtviker.thermopile.di.modules.data.sources;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.sources.local.AccelerationLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.AirQualityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.AltitudeLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.AngularVelocityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.HumidityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.LuminosityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.MagneticFieldLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.PressureLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.SettingsLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.TemperatureLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.ThresholdLocalDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.objectbox.BoxStore;

/**
 * Created by bojan on 12/12/2017.
 */

@Module
public class LocalDataSourceModule {

    @Provides
    @Singleton
    static TemperatureLocalDataSource provideTemperatureLocalDataSource(@NonNull final BoxStore boxStore) {
        return new TemperatureLocalDataSource(boxStore);
    }

    @Provides
    @Singleton
    static HumidityLocalDataSource provideHumidityLocalDataSource(@NonNull final BoxStore boxStore) {
        return new HumidityLocalDataSource(boxStore);
    }

    @Provides
    @Singleton
    static PressureLocalDataSource providePressureLocalDataSource(@NonNull final BoxStore boxStore) {
        return new PressureLocalDataSource(boxStore);
    }

    @Provides
    @Singleton
    static AltitudeLocalDataSource provideAltitudeLocalDataSource(@NonNull final BoxStore boxStore) {
        return new AltitudeLocalDataSource(boxStore);
    }

    @Provides
    @Singleton
    static AirQualityLocalDataSource provideAirQualityLocalDataSource(@NonNull final BoxStore boxStore) {
        return new AirQualityLocalDataSource(boxStore);
    }

    @Provides
    @Singleton
    static LuminosityLocalDataSource provideLuminosityLocalDataSource(@NonNull final BoxStore boxStore) {
        return new LuminosityLocalDataSource(boxStore);
    }

    @Provides
    @Singleton
    static AccelerationLocalDataSource provideAccelerationLocalDataSource(@NonNull final BoxStore boxStore) {
        return new AccelerationLocalDataSource(boxStore);
    }

    @Provides
    @Singleton
    static AngularVelocityLocalDataSource provideAngularVelocityLocalDataSource(@NonNull final BoxStore boxStore) {
        return new AngularVelocityLocalDataSource(boxStore);
    }

    @Provides
    @Singleton
    static MagneticFieldLocalDataSource provideMagneticFieldLocalDataSource(@NonNull final BoxStore boxStore) {
        return new MagneticFieldLocalDataSource(boxStore);
    }

    @Provides
    @Singleton
    static SettingsLocalDataSource provideSettingsLocalDataSource(@NonNull final BoxStore boxStore) {
        return new SettingsLocalDataSource(boxStore);
    }

    @Provides
    @Singleton
    static ThresholdLocalDataSource provideThresholdLocalDataSource(@NonNull final BoxStore boxStore) {
        return new ThresholdLocalDataSource(boxStore);
    }
}
