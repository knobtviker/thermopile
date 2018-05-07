package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Acceleration;
import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.Altitude;
import com.knobtviker.thermopile.data.models.local.AngularVelocity;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Luminosity;
import com.knobtviker.thermopile.data.models.local.MagneticField;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.sources.local.AccelerationLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.AirQualityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.AltitudeLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.AngularVelocityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.HumidityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.LuminosityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.MagneticFieldLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.PressureLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.TemperatureLocalDataSource;
import com.knobtviker.thermopile.data.sources.memory.AccelerationMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.AirQualityMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.AltitudeMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.AngularVelocityMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.HumidityMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.LuminosityMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.MagneticFieldMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.PressureMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.TemperatureMemoryDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.AbstractRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;

/**
 * Created by bojan on 17/07/2017.
 */

public class AtmosphereRepository extends AbstractRepository {

    @Inject
    TemperatureMemoryDataSource temperatureMemoryDataSource;

    @Inject
    HumidityMemoryDataSource humidityMemoryDataSource;

    @Inject
    PressureMemoryDataSource pressureMemoryDataSource;

    @Inject
    AltitudeMemoryDataSource altitudeMemoryDataSource;

    @Inject
    AirQualityMemoryDataSource airQualityMemoryDataSource;

    @Inject
    LuminosityMemoryDataSource luminosityMemoryDataSource;

    @Inject
    AccelerationMemoryDataSource accelerationMemoryDataSource;

    @Inject
    AngularVelocityMemoryDataSource angularVelocityMemoryDataSource;

    @Inject
    MagneticFieldMemoryDataSource magneticFieldMemoryDataSource;

    @Inject
    TemperatureLocalDataSource temperatureLocalDataSource;

    @Inject
    HumidityLocalDataSource humidityLocalDataSource;

    @Inject
    PressureLocalDataSource pressureLocalDataSource;

    @Inject
    AltitudeLocalDataSource altitudeLocalDataSource;

    @Inject
    AirQualityLocalDataSource airQualityLocalDataSource;

    @Inject
    LuminosityLocalDataSource luminosityLocalDataSource;

    @Inject
    AccelerationLocalDataSource accelerationLocalDataSource;

    @Inject
    AngularVelocityLocalDataSource angularVelocityLocalDataSource;

    @Inject
    MagneticFieldLocalDataSource magneticFieldLocalDataSource;

    @Inject
    AtmosphereRepository() {
    }

    public Completable saveTemperatureInMemory(final float item) {
        return temperatureMemoryDataSource
            .save(item)
            .subscribeOn(schedulerProvider.memory)
            .observeOn(schedulerProvider.memory);
    }

    public Completable savePressureInMemory(final float item) {
        return pressureMemoryDataSource
            .save(item)
            .subscribeOn(schedulerProvider.memory)
            .observeOn(schedulerProvider.memory);
    }

    public Completable saveHumidityInMemory(final float item) {
        return humidityMemoryDataSource
            .save(item)
            .subscribeOn(schedulerProvider.memory)
            .observeOn(schedulerProvider.memory);
    }

    public Completable saveAltitudeInMemory(final float item) {
        return altitudeMemoryDataSource
            .save(item)
            .subscribeOn(schedulerProvider.memory)
            .observeOn(schedulerProvider.memory);
    }

    public Completable saveAirQualityInMemory(final float item) {
        return airQualityMemoryDataSource
            .save(item)
            .subscribeOn(schedulerProvider.memory)
            .observeOn(schedulerProvider.memory);
    }

    public Completable saveLuminosityInMemory(final float item) {
        return luminosityMemoryDataSource
            .save(item)
            .subscribeOn(schedulerProvider.memory)
            .observeOn(schedulerProvider.memory);
    }

    public Completable saveAccelerationInMemory(final float[] items) {
        return accelerationMemoryDataSource
            .save(items)
            .subscribeOn(schedulerProvider.memory)
            .observeOn(schedulerProvider.memory);
    }

    public Completable saveAngularVelocityInMemory(final float[] items) {
        return angularVelocityMemoryDataSource
            .save(items)
            .subscribeOn(schedulerProvider.memory)
            .observeOn(schedulerProvider.memory);
    }

    public Completable saveMagneticFieldInMemory(final float[] items) {
        return magneticFieldMemoryDataSource
            .save(items)
            .subscribeOn(schedulerProvider.memory)
            .observeOn(schedulerProvider.memory);
    }

    public Completable saveTemperature(@NonNull final List<Temperature> items) {
        return temperatureLocalDataSource.save(items)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io);
    }

    public Completable savePressure(@NonNull final List<Pressure> items) {
        return pressureLocalDataSource.save(items)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io);
    }

    public Completable saveHumidity(@NonNull final List<Humidity> items) {
        return humidityLocalDataSource.save(items)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io);
    }

    public Completable saveAltitude(@NonNull final List<Altitude> items) {
        return altitudeLocalDataSource.save(items)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io);
    }

    public Completable saveAirQuality(@NonNull final List<AirQuality> items) {
        return airQualityLocalDataSource.save(items)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io);
    }

    public Completable saveLuminosity(@NonNull final List<Luminosity> items) {
        return luminosityLocalDataSource.save(items)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io);
    }

    public Completable saveAccelerations(@NonNull final List<Acceleration> items) {
        return accelerationLocalDataSource.save(items)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io);
    }

    public Completable saveAngularVelocities(@NonNull final List<AngularVelocity> items) {
        return angularVelocityLocalDataSource.save(items)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io);
    }

    public Completable saveMagneticFields(@NonNull final List<MagneticField> items) {
        return magneticFieldLocalDataSource.save(items)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io);
    }
}
