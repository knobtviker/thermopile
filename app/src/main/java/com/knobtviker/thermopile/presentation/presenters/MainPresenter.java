package com.knobtviker.thermopile.presentation.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.data.DaggerAtmosphereDataComponent;
import com.knobtviker.thermopile.di.components.data.DaggerSettingsDataComponent;
import com.knobtviker.thermopile.di.components.data.DaggerThresholdDataComponent;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.presentation.contracts.MainContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import io.reactivex.Observable;
import io.reactivex.android.MainThreadDisposable;

/**
 * Created by bojan on 15/07/2017.
 */

public class MainPresenter extends AbstractPresenter implements MainContract.Presenter {

    private final MainContract.View view;

    private final AtmosphereRepository atmosphereRepository;
    private final SettingsRepository settingsRepository;
    private final ThresholdRepository thresholdRepository;

    public MainPresenter(@NonNull final MainContract.View view) {
        super(view);

        this.view = view;
        this.atmosphereRepository = DaggerAtmosphereDataComponent.create().repository();
        this.settingsRepository = DaggerSettingsDataComponent.create().repository();
        this.thresholdRepository = DaggerThresholdDataComponent.create().repository();
    }

    @Override
    public void observeTemperatureChanged(@NonNull Context context) {
        compositeDisposable.add(
            atmosphereRepository
                .observeTemperature(context)
                .subscribe(
                    view::onTemperatureChanged,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void observePressureChanged(@NonNull Context context) {
        compositeDisposable.add(
            atmosphereRepository
                .observePressure(context)
                .subscribe(
                    view::onPressureChanged,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void observeHumidityChanged(@NonNull Context context) {
        compositeDisposable.add(
            atmosphereRepository
                .observeHumidity(context)
                .subscribe(
                    view::onHumidityChanged,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void observeAirQualityChanged(@NonNull Context context) {
        compositeDisposable.add(
            atmosphereRepository
                .observeAirQuality(context)
                .subscribe(
                    view::onAirQualityChanged,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void observeAccelerationChanged(@NonNull Context context) {
        compositeDisposable.add(
            atmosphereRepository
                .observeAcceleration(context)
                .subscribe(
                    view::onAccelerationChanged,
                    this::error,
                    this::completed
                )
        );
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
    public void settings() {
        started();

        compositeDisposable.add(
            settingsRepository
                .observe()
                .subscribe(settings -> {
                        view.onSettingsChanged(settings.get(0));
                    },
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void thresholds() {
        compositeDisposable.add(
            thresholdRepository
                .loadInline()
                .subscribe(
                    view::onThresholdsChanged,
                    this::error,
                    this::completed
                )
        );
    }
}
