package com.knobtviker.thermopile.presentation.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.MainContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.MainThreadDisposable;

/**
 * Created by bojan on 15/07/2017.
 */

public class MainPresenter extends AbstractPresenter<MainContract.View> implements MainContract.Presenter {

    @NonNull
    private final AtmosphereRepository atmosphereRepository;

    @NonNull
    private final SettingsRepository settingsRepository;

    @NonNull
    private final ThresholdRepository thresholdRepository;

    @Inject
    public MainPresenter(
        @NonNull final MainContract.View view,
        @NonNull final AtmosphereRepository atmosphereRepository,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final ThresholdRepository thresholdRepository,
        @NonNull final Schedulers schedulers
    ) {
        super(view, schedulers);
        this.atmosphereRepository = atmosphereRepository;
        this.settingsRepository = settingsRepository;
        this.thresholdRepository = thresholdRepository;
    }

    @Override
    public void observeDateChanged(@NonNull Context context) {
        compositeDisposable.add(
            Observable.defer(() ->
                Observable.create(emitter -> {
                    final BroadcastReceiver receiver = new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            emitter.onNext(true);
                        }
                    };

                    final IntentFilter filter = new IntentFilter();
                    filter.addAction(Intent.ACTION_DATE_CHANGED);

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
                    this::error
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
    public void settings() {
        compositeDisposable.add(
            settingsRepository
                .observe()
                .subscribe(
                    view::onSettingsChanged,
                    this::error
                )
        );
    }

    @Override
    public void thresholds() {
        compositeDisposable.add(
            thresholdRepository
                .loadInline()
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    view::onThresholdsChanged,
                    this::error
                )
        );
    }
}
