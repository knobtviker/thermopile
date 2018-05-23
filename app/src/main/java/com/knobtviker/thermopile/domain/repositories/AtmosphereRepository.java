package com.knobtviker.thermopile.domain.repositories;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

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
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

import org.joda.time.DateTimeUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposables;

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

    public Observable<Float> observeTemperature(@NonNull final Context context) {
        return observeSingleValue(context, Constants.ACTION_NEW_TEMPERATURE, Constants.KEY_TEMPERATURE)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<Float> observePressure(@NonNull final Context context) {
        return observeSingleValue(context, Constants.ACTION_NEW_PRESSURE, Constants.KEY_PRESSURE)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<Float> observeHumidity(@NonNull final Context context) {
        return observeSingleValue(context, Constants.ACTION_NEW_HUMIDITY, Constants.KEY_HUMIDITY)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<Float> observeAirQuality(@NonNull final Context context) {
        return observeSingleValue(context, Constants.ACTION_NEW_AIR_QUALITY, Constants.KEY_AIR_QUALITY)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<float[]> observeAcceleration(@NonNull final Context context) {
        return observeCartesianValue(context, Constants.ACTION_NEW_ACCELERATION, Constants.KEY_ACCELERATION)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    private Observable<Float> observeSingleValue(@NonNull final Context context, @NonNull final String action, @NonNull final String key) {
        return Observable.defer(() ->
            Observable.create((ObservableEmitter<Float> emitter) -> {
                final IntentFilter filter = new IntentFilter();
                filter.addAction(String.format("%s.%s", context.getApplicationContext().getPackageName(), action));

                final WeakReference<LocalBroadcastManager> localBroadcastManagerWeakReference = new WeakReference<>(LocalBroadcastManager.getInstance(context.getApplicationContext()));

                final BroadcastReceiver receiver = new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.hasExtra(key)) {
                            final float value = intent.getFloatExtra(key, 0.0f);

                            emitter.onNext(value);
                        } else {
                            emitter.onError(new NoSuchFieldException());
                        }
                    }
                };

                if (localBroadcastManagerWeakReference.get() != null) {
                    localBroadcastManagerWeakReference.get().registerReceiver(receiver, filter);
                }

                emitter.setDisposable(Disposables.fromRunnable(() -> {
                    if (localBroadcastManagerWeakReference.get() != null) {
                        localBroadcastManagerWeakReference.get().unregisterReceiver(receiver);
                    }
                }));
            })
        );
    }

    private Observable<float[]> observeCartesianValue(@NonNull final Context context, @NonNull final String action, @NonNull final String key) {
        return Observable.defer(() ->
            Observable.create((ObservableEmitter<float[]> emitter) -> {
                final IntentFilter filter = new IntentFilter();
                filter.addAction(String.format("%s.%s", context.getApplicationContext().getPackageName(), action));

                final WeakReference<LocalBroadcastManager> localBroadcastManagerWeakReference = new WeakReference<>(LocalBroadcastManager.getInstance(context.getApplicationContext()));

                final BroadcastReceiver receiver = new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.hasExtra(key)) {
                            final float[] values = intent.getFloatArrayExtra(key);

                            emitter.onNext(values);
                        } else {
                            emitter.onError(new NoSuchFieldException());
                        }
                    }
                };

                if (localBroadcastManagerWeakReference.get() != null) {
                    localBroadcastManagerWeakReference.get().registerReceiver(receiver, filter);
                }

                emitter.setDisposable(Disposables.fromRunnable(() -> {
                    if (localBroadcastManagerWeakReference.get() != null) {
                        localBroadcastManagerWeakReference.get().unregisterReceiver(receiver);
                    }
                }));
            })
        );
    }

    private float convertPressureToAltitude(final float value) {
        return SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, value);
    }

    public Observable<List<Temperature>> loadTemperature() {
        return temperatureLocalDataSource
            .query()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<List<Humidity>> loadHumidity() {
        return humidityLocalDataSource
            .query()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<List<Pressure>> loadPressure() {
        return pressureLocalDataSource
            .query()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<List<AirQuality>> loadAirQuality() {
        return airQualityLocalDataSource
            .query()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<List<Acceleration>> loadAcceleration() {
        return accelerationLocalDataSource
            .query()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }
}
