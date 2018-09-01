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
import com.knobtviker.thermopile.domain.shared.base.AbstractRepository;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by bojan on 17/07/2017.
 */

public class AtmosphereRepository extends AbstractRepository {

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

    public Completable saveTemperature(@NonNull final String vendor, @NonNull final String name, final float value) {
        return temperatureLocalDataSource
            .save(new Temperature(DateTimeKit.instant().toEpochMilli(), vendor, name, value))
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.io)
            .ignoreElements();
    }

    public Completable savePressure(@NonNull final String vendor, @NonNull final String name, final float value) {
        return pressureLocalDataSource
            .save(new Pressure(DateTimeKit.instant().toEpochMilli(), vendor, name, value))
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.io)
            .ignoreElements();
    }

    public Completable saveAltitude(@NonNull final String vendor, @NonNull final String name, final float value) {
        return altitudeLocalDataSource
            .save(new Altitude(DateTimeKit.instant().toEpochMilli(), vendor, name, convertPressureToAltitude(value)))
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.io)
            .ignoreElements();
    }

    public Completable saveHumidity(@NonNull final String vendor, @NonNull final String name, final float value) {
        return humidityLocalDataSource
            .save(new Humidity(DateTimeKit.instant().toEpochMilli(), vendor, name, value))
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.io)
            .ignoreElements();
    }

    public Completable saveAirQuality(@NonNull final String vendor, @NonNull final String name, final float value) {
        return airQualityLocalDataSource
            .save(new AirQuality(DateTimeKit.instant().toEpochMilli(), vendor, name, value))
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.io)
            .ignoreElements();
    }

    public Completable saveLuminosity(@NonNull final String vendor, @NonNull final String name, final float value) {
        return luminosityLocalDataSource
            .save(new Luminosity(DateTimeKit.instant().toEpochMilli(), vendor, name, value))
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.io)
            .ignoreElements();
    }

    public Completable saveAcceleration(@NonNull final String vendor, @NonNull final String name, final float[] values) {
        return accelerationLocalDataSource
            .save(new Acceleration(DateTimeKit.instant().toEpochMilli(), vendor, name, values[0], values[1], values[2]))
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.io)
            .ignoreElements();
    }

    public Completable saveAngularVelocity(@NonNull final String vendor, @NonNull final String name, final float[] values) {
        return angularVelocityLocalDataSource
            .save(new AngularVelocity(DateTimeKit.instant().toEpochMilli(), vendor, name, values[0], values[1], values[2]))
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.io)
            .ignoreElements();
    }

    public Completable saveMagneticField(@NonNull final String vendor, @NonNull final String name, final float[] values) {
        return magneticFieldLocalDataSource
            .save(new MagneticField(DateTimeKit.instant().toEpochMilli(), vendor, name, values[0], values[1], values[2]))
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.io)
            .ignoreElements();
    }

    public Flowable<Float> observeTemperature() {
        return temperatureLocalDataSource
            .observe()
            .subscribeOn(schedulers.io)
            .map(item -> item.value)
            .observeOn(schedulers.ui);
    }

    public Flowable<Float> observePressure() {
        return pressureLocalDataSource
            .observe()
            .subscribeOn(schedulers.io)
            .map(item -> item.value)
            .observeOn(schedulers.ui);
    }

    public Flowable<Float> observeHumidity() {
        return humidityLocalDataSource
            .observe()
            .subscribeOn(schedulers.io)
            .map(item -> item.value)
            .observeOn(schedulers.ui);
    }

    public Flowable<Float> observeAirQuality() {
        return airQualityLocalDataSource
            .observe()
            .subscribeOn(schedulers.io)
            .map(item -> item.value)
            .observeOn(schedulers.ui);
    }

    public Flowable<float[]> observeAcceleration() {
        return accelerationLocalDataSource
            .observe()
            .subscribeOn(schedulers.io)
            .map(item -> new float[]{item.valueX, item.valueY, item.valueZ})
            .observeOn(schedulers.ui);
    }

    public Observable<List<Temperature>> loadTemperatureBetween(final long start, final long end) {
        return temperatureLocalDataSource
            .queryBetween(start, end)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui);
    }

    public Observable<List<Humidity>> loadHumidityBetween(final long start, final long end) {
        return humidityLocalDataSource
            .queryBetween(start, end)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui);
    }

    public Observable<List<Pressure>> loadPressureBetween(final long start, final long end) {
        return pressureLocalDataSource
            .queryBetween(start, end)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui);
    }

    public Observable<List<AirQuality>> loadAirQualityBetween(final long start, final long end) {
        return airQualityLocalDataSource
            .queryBetween(start, end)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui);
    }

    public Observable<List<Acceleration>> loadAccelerationBetween(final long start, final long end) {
        return accelerationLocalDataSource
            .queryBetween(start, end)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui);
    }

    private float convertPressureToAltitude(final float value) {
        return SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, value);
    }
}
