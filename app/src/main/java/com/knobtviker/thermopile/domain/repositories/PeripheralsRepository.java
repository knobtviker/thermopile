package com.knobtviker.thermopile.domain.repositories;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.knobtviker.android.things.contrib.community.boards.I2CDevice;
import com.knobtviker.thermopile.data.models.local.Acceleration;
import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.Altitude;
import com.knobtviker.thermopile.data.models.local.AngularVelocity;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Luminosity;
import com.knobtviker.thermopile.data.models.local.MagneticField;
import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.sources.local.PeripheralLocalDataSource;
import com.knobtviker.thermopile.data.sources.raw.PeripheralRawDataSource;
import com.knobtviker.thermopile.data.sources.raw.rxsensormanager.RxSensorEvent;
import com.knobtviker.thermopile.data.sources.raw.rxsensormanager.RxSensorManager;
import com.knobtviker.thermopile.domain.repositories.implementation.AbstractRepository;
import com.knobtviker.thermopile.presentation.utils.Constants;

import org.joda.time.DateTimeUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposables;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 17/07/2017.
 */

public class PeripheralsRepository extends AbstractRepository {

    private static final int BATCH_SIZE = 100;

    @Inject
    PeripheralLocalDataSource peripheralLocalDataSource;

    @Inject
    PeripheralRawDataSource peripheralRawDataSource;

    @Inject
    PeripheralsRepository() {
    }

    public List<I2CDevice> probe() {
        return peripheralRawDataSource.load();
    }

    public RealmResults<PeripheralDevice> load(@NonNull final Realm realm) {
        return peripheralLocalDataSource.load(realm);
    }

    @Nullable
    public PeripheralDevice loadById(@NonNull final Realm realm, @NonNull final String id) {
        return peripheralLocalDataSource.loadById(realm, id);
    }

    public void save(@NonNull final List<PeripheralDevice> foundSensors) {
        peripheralLocalDataSource.save(foundSensors);
    }

    public void saveConnected(@NonNull final List<PeripheralDevice> items, final boolean isConnected) {
        peripheralLocalDataSource.saveConnected(items, isConnected);
    }

    public void saveEnabled(@NonNull final PeripheralDevice item, final int type, final boolean isEnabled) {
        peripheralLocalDataSource.saveEnabled(item, type, isEnabled);
    }

    public Flowable<SensorEvent> observeSensors(@NonNull final Context context) {
        return new RxSensorManager(context)
            .observeSensors(SensorManager.SENSOR_DELAY_UI)
            .subscribeOn(schedulerProvider.computation)
            .filter(RxSensorEvent::hasSensorEvent)
            .map(RxSensorEvent::getSensorEvent)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<Float> observeTemperature(@NonNull final Context context) {
        return observeSingleValue(context, Constants.ACTION_NEW_TEMPERATURE, Constants.KEY_TEMPERATURE)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged()
            .observeOn(schedulerProvider.ui);
    }

    public Observable<Float> observePressure(@NonNull final Context context) {
        return observeSingleValue(context, Constants.ACTION_NEW_PRESSURE, Constants.KEY_PRESSURE)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged()
            .observeOn(schedulerProvider.ui);
    }

    public Observable<Float> observeHumidity(@NonNull final Context context) {
        return observeSingleValue(context, Constants.ACTION_NEW_HUMIDITY, Constants.KEY_HUMIDITY)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged()
            .observeOn(schedulerProvider.ui);
    }

    public Observable<Float> observeAirQuality(@NonNull final Context context) {
        return observeSingleValue(context, Constants.ACTION_NEW_AIR_QUALITY, Constants.KEY_AIR_QUALITY)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged()
            .observeOn(schedulerProvider.ui);
    }

    public Observable<float[]> observeAcceleration(@NonNull final Context context) {
        return observeCartesianValue(context, Constants.ACTION_NEW_ACCELERATION, Constants.KEY_ACCELERATION)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged()
            .observeOn(schedulerProvider.ui);
    }

    public Observable<List<Temperature>> observeTemperatureBuffered(@NonNull final Context context) {
        return observeSingleValue(context, Constants.ACTION_NEW_TEMPERATURE, Constants.KEY_TEMPERATURE)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged()
            .map(value -> new Temperature(DateTimeUtils.currentTimeMillis(), value))
            .buffer(BATCH_SIZE)
            .observeOn(schedulerProvider.io);
    }

    public Observable<List<Pressure>> observePressureBuffered(@NonNull final Context context) {
        return observeSingleValue(context, Constants.ACTION_NEW_PRESSURE, Constants.KEY_PRESSURE)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged()
            .map(value -> new Pressure(DateTimeUtils.currentTimeMillis(), value))
            .buffer(BATCH_SIZE)
            .observeOn(schedulerProvider.io);
    }

    public Observable<List<Altitude>> observeAltitudeBuffered(@NonNull final Context context) {
        return observeSingleValue(context, Constants.ACTION_NEW_ALTITUDE, Constants.KEY_ALTITUDE)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged()
            .map(value -> new Altitude(DateTimeUtils.currentTimeMillis(), value))
            .buffer(BATCH_SIZE)
            .observeOn(schedulerProvider.io);
    }

    public Observable<List<Humidity>> observeHumidityBuffered(@NonNull final Context context) {
        return observeSingleValue(context, Constants.ACTION_NEW_HUMIDITY, Constants.KEY_HUMIDITY)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged()
            .map(value -> new Humidity(DateTimeUtils.currentTimeMillis(), value))
            .buffer(BATCH_SIZE)
            .observeOn(schedulerProvider.io);
    }

    public Observable<List<AirQuality>> observeAirQualityBuffered(@NonNull final Context context) {
        return observeSingleValue(context, Constants.ACTION_NEW_AIR_QUALITY, Constants.KEY_AIR_QUALITY)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged()
            .map(value -> new AirQuality(DateTimeUtils.currentTimeMillis(), value))
            .buffer(BATCH_SIZE)
            .observeOn(schedulerProvider.io);
    }

    public Observable<List<Luminosity>> observeLuminosityBuffered(@NonNull final Context context) {
        return observeSingleValue(context, Constants.ACTION_NEW_LUMINOSITY, Constants.KEY_LUMINOSITY)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged()
            .map(value -> new Luminosity(DateTimeUtils.currentTimeMillis(), value))
            .buffer(BATCH_SIZE)
            .observeOn(schedulerProvider.io);
    }

    public Observable<List<Acceleration>> observeAccelerationBuffered(@NonNull final Context context) {
        return observeCartesianValue(context, Constants.ACTION_NEW_ACCELERATION, Constants.KEY_ACCELERATION)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged((floatsOld, floatsNew) -> Math.sqrt(Math.pow(floatsOld[0], 2) + Math.pow(floatsOld[1], 2) + Math.pow(floatsOld[2], 2)) == Math.sqrt(Math.pow(floatsNew[0], 2) + Math.pow(floatsNew[1], 2) + Math.pow(floatsNew[2], 2)))
            .map(values -> new Acceleration(DateTimeUtils.currentTimeMillis(), values[0], values[1], values[2]))
            .buffer(BATCH_SIZE)
            .observeOn(schedulerProvider.io);
    }

    public Observable<List<AngularVelocity>> observeAngularVelocityBuffered(@NonNull final Context context) {
        return observeCartesianValue(context, Constants.ACTION_NEW_ANGULAR_VELOCITY, Constants.KEY_ANGULAR_VELOCITY)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged((floatsOld, floatsNew) -> Math.sqrt(Math.pow(floatsOld[0], 2) + Math.pow(floatsOld[1], 2) + Math.pow(floatsOld[2], 2)) == Math.sqrt(Math.pow(floatsNew[0], 2) + Math.pow(floatsNew[1], 2) + Math.pow(floatsNew[2], 2)))
            .map(values -> new AngularVelocity(DateTimeUtils.currentTimeMillis(), values[0], values[1], values[2]))
            .buffer(BATCH_SIZE)
            .observeOn(schedulerProvider.io);
    }

    public Observable<List<MagneticField>> observeMagneticFieldBuffered(@NonNull final Context context) {
        return observeCartesianValue(context, Constants.ACTION_NEW_MAGNETIC_FIELD, Constants.KEY_MAGNETIC_FIELD)
            .subscribeOn(schedulerProvider.io)
            .distinctUntilChanged((floatsOld, floatsNew) -> Math.sqrt(Math.pow(floatsOld[0], 2) + Math.pow(floatsOld[1], 2) + Math.pow(floatsOld[2], 2)) == Math.sqrt(Math.pow(floatsNew[0], 2) + Math.pow(floatsNew[1], 2) + Math.pow(floatsNew[2], 2)))
            .map(values -> new MagneticField(DateTimeUtils.currentTimeMillis(), values[0], values[1], values[2]))
            .buffer(BATCH_SIZE)
            .observeOn(schedulerProvider.io);
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

                            emitter.onNext(normalized(value));
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
        )
            .startWith(0.0f);
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

                            emitter.onNext(new float[]{
                                normalizedToScale(values[0]),
                                normalizedToScale(values[1]),
                                normalizedToScale(values[2])
                            });
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
        )
            .startWith(new float[]{0.0f, 0.0f, 0.0f});
    }

    private float normalized(final float input) {
        return Math.round(input);
    }

    private float normalizedToScale(final float input) {
        return Math.round(input * 10) / 10;
    }
}
