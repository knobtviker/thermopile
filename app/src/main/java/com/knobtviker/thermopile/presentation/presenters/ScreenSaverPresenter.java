package com.knobtviker.thermopile.presentation.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.data.DaggerPeripheralsDataComponent;
import com.knobtviker.thermopile.di.components.data.DaggerSettingsDataComponent;
import com.knobtviker.thermopile.domain.repositories.PeripheralsRepository;
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

    private final PeripheralsRepository peripheralsRepository;
    private final SettingsRepository settingsRepository;

    public ScreenSaverPresenter(@NonNull final ScreenSaverContract.View view) {
        super(view);

        this.view = view;
        this.peripheralsRepository = DaggerPeripheralsDataComponent.create().repository();
        this.settingsRepository = DaggerSettingsDataComponent.create().repository();
    }

    @Override
    public void observeTemperatureChanged(@NonNull Context context) {
        compositeDisposable.add(
            peripheralsRepository.observeTemperature(context)
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
            peripheralsRepository.observePressure(context)
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
            peripheralsRepository.observeHumidity(context)
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
            peripheralsRepository.observeAirQuality(context)
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
            peripheralsRepository
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
                .load()
                .subscribe(
                    settings -> {
                        view.onSettingsChanged(settings.get(0));
                    },
                    this::error,
                    this::completed
                )
        );
    }
}
