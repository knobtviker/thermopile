package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.ChartsContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;
import com.knobtviker.thermopile.presentation.shared.constants.charts.ChartType;

import javax.inject.Inject;

/**
 * Created by bojan on 15/07/2017.
 */

public class ChartsPresenter extends AbstractPresenter<ChartsContract.View> implements ChartsContract.Presenter {

    @NonNull
    private final SettingsRepository settingsRepository;

    @NonNull
    private final AtmosphereRepository atmosphereRepository;

    @Inject
    public ChartsPresenter(
        @NonNull final ChartsContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final AtmosphereRepository atmosphereRepository,
        @NonNull final Schedulers schedulers
    ) {
        super(view, schedulers);
        this.settingsRepository = settingsRepository;
        this.atmosphereRepository = atmosphereRepository;
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
    public void data(@ChartType int type, long startTimestamp, long endTimestamp) {
        switch (type) {
            case ChartType.TEMPERATURE:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadTemperatureBetween(startTimestamp, endTimestamp)
                        .doOnSubscribe(consumer -> subscribed())
                        .doOnTerminate(this::terminated)
                        .subscribe(
                            view::onTemperature,
                            this::error
                        )
                );
                break;
            case ChartType.HUMIDITY:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadHumidityBetween(startTimestamp, endTimestamp)
                        .doOnSubscribe(consumer -> subscribed())
                        .doOnTerminate(this::terminated)
                        .subscribe(
                            view::onHumidity,
                            this::error
                        )
                );
                break;
            case ChartType.PRESSURE:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadPressureBetween(startTimestamp, endTimestamp)
                        .doOnSubscribe(consumer -> subscribed())
                        .doOnTerminate(this::terminated)
                        .subscribe(
                            view::onPressure,
                            this::error
                        )
                );
                break;
            case ChartType.AIR_QUALITY:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadAirQualityBetween(startTimestamp, endTimestamp)
                        .doOnSubscribe(consumer -> subscribed())
                        .doOnTerminate(this::terminated)
                        .subscribe(
                            view::onAirQuality,
                            this::error
                        )
                );
                break;
            case ChartType.MOTION:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadAccelerationBetween(startTimestamp, endTimestamp)
                        .doOnSubscribe(consumer -> subscribed())
                        .doOnTerminate(this::terminated)
                        .subscribe(
                            view::onMotion,
                            this::error
                        )
                );
                break;
        }
    }
}
