package com.knobtviker.thermopile.presentation.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.knobtviker.thermopile.di.components.domain.repositories.DaggerAtmosphereRepositoryComponent;
import com.knobtviker.thermopile.di.modules.data.sources.local.AtmosphereLocalDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.presentation.contracts.NetworkContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;
import com.knobtviker.thermopile.shared.constants.Keys;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposables;

/**
 * Created by bojan on 15/07/2017.
 */

public class NetworkPresenter extends AbstractPresenter implements NetworkContract.Presenter {

    private final NetworkContract.View view;

    private final AtmosphereRepository atmosphereRepository;

    public NetworkPresenter(@NonNull final Context context, @NonNull final NetworkContract.View view) {
        super(view);

        this.view = view;
        this.atmosphereRepository = DaggerAtmosphereRepositoryComponent.builder()
            .localDataSource(new AtmosphereLocalDataSourceModule())
            .build()
            .inject();
    }

    @Override
    public void observeTemperature() {
        compositeDisposable.add(
            atmosphereRepository
                .observeTemperature()
                .subscribe(
                    view::onTemperatureChanged,
                    this::error
                )
        );
    }

    @Override
    public void observePressure() {
        compositeDisposable.add(
            atmosphereRepository
                .observePressure()
                .subscribe(
                    view::onPressureChanged,
                    this::error
                )
        );
    }

    @Override
    public void observeHumidity() {
        compositeDisposable.add(
            atmosphereRepository
                .observeHumidity()
                .subscribe(
                    view::onHumidityChanged,
                    this::error
                )
        );
    }

    @Override
    public void observeAirQuality() {
        compositeDisposable.add(
            atmosphereRepository
                .observeAirQuality()
                .subscribe(
                    view::onAirQualityChanged,
                    this::error
                )
        );
    }

    @Override
    public void observeAcceleration() {
        compositeDisposable.add(
            atmosphereRepository
                .observeAcceleration()
                .subscribe(
                    view::onAccelerationChanged,
                    this::error
                )
        );
    }

    @Override
    public void observeBluetoothState(@NonNull Context context) {
        compositeDisposable.add(
        Observable.create((ObservableEmitter<Integer> emitter) -> {
            final IntentFilter filter = new IntentFilter();
            filter.addAction(String.format("%s.%s", context.getApplicationContext().getPackageName(), Keys.BLUETOOTH_STATE.toUpperCase()));

            final WeakReference<LocalBroadcastManager> localBroadcastManagerWeakReference = new WeakReference<>(LocalBroadcastManager.getInstance(context.getApplicationContext()));

            final BroadcastReceiver receiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.hasExtra(Keys.BLUETOOTH_STATE)) {

                        emitter.onNext(intent.getIntExtra(Keys.BLUETOOTH_STATE, -1));
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
            .subscribe(

            )
        );
    }
}
