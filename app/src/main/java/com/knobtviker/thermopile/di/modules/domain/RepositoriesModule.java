package com.knobtviker.thermopile.di.modules.domain;

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
import com.knobtviker.thermopile.data.sources.raw.bluetooth.BluetoothRawDataSource;
import com.knobtviker.thermopile.data.sources.raw.network.WifiRawDataSource;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.NetworkRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoriesModule {

    @Provides
    @Singleton
    static AtmosphereRepository provideAtmosphereRepository(
        @NonNull final TemperatureLocalDataSource temperatureLocalDataSource,
        @NonNull final HumidityLocalDataSource humidityLocalDataSource,
        @NonNull final PressureLocalDataSource pressureLocalDataSource,
        @NonNull final AltitudeLocalDataSource altitudeLocalDataSource,
        @NonNull final AirQualityLocalDataSource airQualityLocalDataSource,
        @NonNull final LuminosityLocalDataSource luminosityLocalDataSource,
        @NonNull final AccelerationLocalDataSource accelerationLocalDataSource,
        @NonNull final AngularVelocityLocalDataSource angularVelocityLocalDataSource,
        @NonNull final MagneticFieldLocalDataSource magneticFieldLocalDataSource,
        @NonNull final Schedulers schedulers
    ) {
        return new AtmosphereRepository(
            temperatureLocalDataSource,
            humidityLocalDataSource,
            pressureLocalDataSource,
            altitudeLocalDataSource,
            airQualityLocalDataSource,
            luminosityLocalDataSource,
            accelerationLocalDataSource,
            angularVelocityLocalDataSource,
            magneticFieldLocalDataSource,
            schedulers
        );
    }

    @Provides
    @Singleton
    static NetworkRepository provideNetworkRepository(
        @NonNull final BluetoothRawDataSource bluetoothRawDataSource,
        @NonNull final WifiRawDataSource wifiRawDataSource,
        @NonNull final Schedulers schedulers
    ) {
        return new NetworkRepository(bluetoothRawDataSource, wifiRawDataSource, schedulers);
    }

    @Provides
    @Singleton
    static SettingsRepository provideSettingsRepository(
        @NonNull final SettingsLocalDataSource settingsLocalDataSource,
        @NonNull final Schedulers schedulers
    ) {
        return new SettingsRepository(settingsLocalDataSource, schedulers);
    }

    @Provides
    @Singleton
    static ThresholdRepository provideThresholdRepository(
        @NonNull final ThresholdLocalDataSource thresholdLocalDataSource,
        @NonNull final Schedulers schedulers
    ) {
        return new ThresholdRepository(thresholdLocalDataSource, schedulers);
    }
}
