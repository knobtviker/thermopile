package com.knobtviker.thermopile.domain.repositories;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.knobtviker.android.things.contrib.community.boards.I2CDevice;
import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.data.sources.local.PeripheralLocalDataSource;
import com.knobtviker.thermopile.data.sources.raw.PeripheralRawDataSource;
import com.knobtviker.thermopile.domain.repositories.implementation.AbstractRepository;
import com.knobtviker.thermopile.presentation.utils.Constants;

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

public class PeripheralsRepository extends AbstractRepository {

    private static final int BATCH_SIZE = 1000;

    @Inject
    PeripheralLocalDataSource peripheralLocalDataSource;

    @Inject
    PeripheralRawDataSource peripheralRawDataSource;

    @Inject
    PeripheralsRepository() {
    }

    public Observable<List<I2CDevice>> probe() {
        return Observable
            .just(peripheralRawDataSource.load())
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io);
    }

    public Observable<List<PeripheralDevice>> load() {
        return peripheralLocalDataSource.query()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io);
    }

    public Observable<PeripheralDevice> loadById(final long id) {
        return peripheralLocalDataSource
            .queryById(id)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io);
    }

    public Completable save(@NonNull final List<PeripheralDevice> foundSensors) {
        return peripheralLocalDataSource.save(foundSensors)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io);
    }

    public Completable saveConnected(@NonNull final List<PeripheralDevice> items, final boolean isConnected) {
        return peripheralLocalDataSource
            .saveConnected(items, isConnected)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<Long> saveEnabled(@NonNull final PeripheralDevice item, final int type, final boolean isEnabled) {
        return peripheralLocalDataSource
            .saveEnabled(item, type, isEnabled)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io);
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
}
