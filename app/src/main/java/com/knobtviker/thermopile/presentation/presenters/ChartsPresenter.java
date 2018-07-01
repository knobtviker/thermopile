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
            .repository();
        this.atmosphereRepository = DaggerAtmosphereRepositoryComponent.builder()
            .localDataSource(new AtmosphereLocalDataSourceModule())
            .build()
            .repository();
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

    @Override
    public void data(int type, long startTimestamp, long endTimestamp) {
        started();

        switch (type) {
            case 0:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadTemperatureBetween(startTimestamp, endTimestamp)
                        .subscribe(
                            view::onTemperature,
                            this::error,
                            this::completed
                        )
                );
                break;
            case 1:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadHumidityBetween(startTimestamp, endTimestamp)
                        .subscribe(
                            view::onHumidity,
                            this::error,
                            this::completed
                        )
                );
                break;
            case 2:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadPressureBetween(startTimestamp, endTimestamp)
                        .subscribe(
                            view::onPressure,
                            this::error,
                            this::completed
                        )
                );
                break;
            case 3:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadAirQualityBetween(startTimestamp, endTimestamp)
                        .subscribe(
                            view::onAirQuality,
                            this::error,
                            this::completed
                        )
                );
                break;
            case 4:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadAccelerationBetween(startTimestamp, endTimestamp)
                        .subscribe(
                            view::onMotion,
                            this::error,
                            this::completed
                        )
                );
                break;
        }
    }
}
