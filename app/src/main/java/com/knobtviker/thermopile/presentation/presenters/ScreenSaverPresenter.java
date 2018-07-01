package com.knobtviker.thermopile.presentation.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.domain.repositories.DaggerAtmosphereRepositoryComponent;
import com.knobtviker.thermopile.di.components.domain.repositories.DaggerSettingsRepositoryComponent;
import com.knobtviker.thermopile.di.modules.data.sources.local.AtmosphereLocalDataSourceModule;
import com.knobtviker.thermopile.di.modules.data.sources.local.SettingsLocalDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.ScreenSaverContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import io.reactivex.Observable;
import io.reactivex.android.MainThreadDisposable;

/**
 * Created by bojan on 15/07/2017.
 */

public class ScreenSaverPresenter extends AbstractPresenter implements ScreenSaverContract.Presenter {

    private final ScreenSaverContract.View view;

    private final AtmosphereRepository atmosphereRepository;
    private final SettingsRepository settingsRepository;

    public ScreenSaverPresenter(@NonNull final ScreenSaverContract.View view) {
        super(view);

        this.view = view;
        this.atmosphereRepository = DaggerAtmosphereRepositoryComponent.builder()
            .localDataSource(new AtmosphereLocalDataSourceModule())
            .build()
            .repository();
        this.settingsRepository = DaggerSettingsRepositoryComponent.builder()
            .localDataSource(new SettingsLocalDataSourceModule())
            .build()
            .repository();
    }

    @Override
    public void observeDateChanged(@NonNull Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);

        compositeDisposable.add(
            Observable.defer(() ->
                Observable.create(emitter -> {
                    final BroadcastReceiver receiver = new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            emitter.onNext(true);
                        }
                    };

                    context.registerReceiver(receiver, filter);

                    emitter.setDisposable(new MainThreadDisposable() {
                        @Override
                        protected void onDispose() {
                            context.unregisterReceiver(receiver);

                            dispose();
                        }
                    });
                })
            )
                .subscribe(
                    item -> view.onDateChanged(),
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void observeTemperature() {
        compositeDisposable.add(
            atmosphereRepository
                .observeTemperature()
                .subscribe(
                    view::onTemperatureChanged,
                    this::error,
                    this::completed
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
                    this::error,
                    this::completed
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
                    this::error,
                    this::completed
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
                    this::error,
                    this::completed
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
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void settings() {
        started();

        compositeDisposable.add(
            settingsRepository
                .observe()
                .subscribe(
                    view::onSettingsChanged,
                    this::error,
                    this::completed
                )
        );
    }
}
