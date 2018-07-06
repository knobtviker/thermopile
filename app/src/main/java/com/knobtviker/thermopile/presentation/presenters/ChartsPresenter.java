package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.domain.repositories.DaggerAtmosphereRepositoryComponent;
import com.knobtviker.thermopile.di.components.domain.repositories.DaggerSettingsRepositoryComponent;
import com.knobtviker.thermopile.di.modules.data.sources.local.AtmosphereLocalDataSourceModule;
import com.knobtviker.thermopile.di.modules.data.sources.local.SettingsLocalDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.ChartsContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

/**
 * Created by bojan on 15/07/2017.
 */

public class ChartsPresenter extends AbstractPresenter implements ChartsContract.Presenter {

    private final ChartsContract.View view;

    private final SettingsRepository settingsRepository;
    private final AtmosphereRepository atmosphereRepository;

    public ChartsPresenter(@NonNull final ChartsContract.View view) {
        super(view);

        this.view = view;
        this.settingsRepository = DaggerSettingsRepositoryComponent.builder()
            .localDataSource(new SettingsLocalDataSourceModule())
            .build()
            .inject();
        this.atmosphereRepository = DaggerAtmosphereRepositoryComponent.builder()
            .localDataSource(new AtmosphereLocalDataSourceModule())
            .build()
            .inject();
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
    public void data(int type, long startTimestamp, long endTimestamp) {
        switch (type) {
            case 0:
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
            case 1:
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
            case 2:
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
            case 3:
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
            case 4:
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
