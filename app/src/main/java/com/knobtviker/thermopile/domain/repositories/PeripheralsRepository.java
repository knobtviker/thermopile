package com.knobtviker.thermopile.domain.repositories;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.knobtviker.thermopile.domain.repositories.implementation.AbstractRepository;
import com.knobtviker.thermopile.presentation.utils.Constants;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposables;

/**
 * Created by bojan on 17/07/2017.
 */

public class PeripheralsRepository extends AbstractRepository {

    @Inject
    PeripheralsRepository() {
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
