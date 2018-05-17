package com.knobtviker.thermopile.domain.repositories;

import android.hardware.SensorManager;
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

import org.joda.time.DateTimeUtils;

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

    public Completable saveAltitudeInMemory(final float item) {
        return altitudeMemoryDataSource
            .save(convertPressureToAltitude(item))
            .subscribeOn(schedulerProvider.memory)
            .observeOn(schedulerProvider.memory);
    }

    public Completable saveHumidityInMemory(final float item) {
        return humidityMemoryDataSource
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

    public Completable saveTemperature(@NonNull final String vendor, @NonNull final String name, final float value) {
        return temperatureLocalDataSource
            .save(new Temperature(DateTimeUtils.currentTimeMillis(), vendor, name, value))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    public Completable savePressure(@NonNull final String vendor, @NonNull final String name, final float value) {
        return pressureLocalDataSource
            .save(new Pressure(DateTimeUtils.currentTimeMillis(), vendor, name, value))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    public Completable saveAltitude(@NonNull final String vendor, @NonNull final String name, final float value) {
        return altitudeLocalDataSource
            .save(new Altitude(DateTimeUtils.currentTimeMillis(), vendor, name, convertPressureToAltitude(value)))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    public Completable saveHumidity(@NonNull final String vendor, @NonNull final String name, final float value) {
        return humidityLocalDataSource
            .save(new Humidity(DateTimeUtils.currentTimeMillis(), vendor, name, value))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    public Completable saveAirQuality(@NonNull final String vendor, @NonNull final String name, final float value) {
        return airQualityLocalDataSource
            .save(new AirQuality(DateTimeUtils.currentTimeMillis(), vendor, name, value))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    public Completable saveLuminosity(@NonNull final String vendor, @NonNull final String name, final float value) {
        return luminosityLocalDataSource
            .save(new Luminosity(DateTimeUtils.currentTimeMillis(), vendor, name, value))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    public Completable saveAcceleration(@NonNull final String vendor, @NonNull final String name, final float[] values) {
        return accelerationLocalDataSource
            .save(new Acceleration(DateTimeUtils.currentTimeMillis(), vendor, name, values[0], values[1], values[2]))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    public Completable saveAngularVelocity(@NonNull final String vendor, @NonNull final String name, final float[] values) {
        return angularVelocityLocalDataSource
            .save(new AngularVelocity(DateTimeUtils.currentTimeMillis(), vendor, name, values[0], values[1], values[2]))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    public Completable saveMagneticField(@NonNull final String vendor, @NonNull final String name, final float[] values) {
        return magneticFieldLocalDataSource
            .save(new MagneticField(DateTimeUtils.currentTimeMillis(), vendor, name, values[0], values[1], values[2]))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .ignoreElements();
    }

    private float convertPressureToAltitude(final float value) {
        return SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, value);
    }
}
