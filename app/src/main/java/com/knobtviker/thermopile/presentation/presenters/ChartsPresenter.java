package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.data.DaggerAtmosphereDataComponent;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.presentation.contracts.ChartsContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

/**
 * Created by bojan on 15/07/2017.
 */

public class ChartsPresenter extends AbstractPresenter implements ChartsContract.Presenter {

    private final ChartsContract.View view;

    private final AtmosphereRepository atmosphereRepository;

    public ChartsPresenter(@NonNull final ChartsContract.View view) {
        super(view);

        this.view = view;
        this.atmosphereRepository = DaggerAtmosphereDataComponent.create().repository();
    }

    @Override
    public void data(int type, int interval) {
        started();

        switch (type) {
            case 0:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadTemperature()
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
                        .loadHumidity()
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
                        .loadPressure()
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
                        .loadAirQuality()
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
                        .loadAcceleration()
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
