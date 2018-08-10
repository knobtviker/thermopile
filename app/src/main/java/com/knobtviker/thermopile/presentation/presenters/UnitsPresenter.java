package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.domain.repositories.DaggerSettingsRepositoryComponent;
import com.knobtviker.thermopile.di.modules.data.sources.local.SettingsLocalDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.UnitsContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;

import io.reactivex.internal.functions.Functions;

/**
 * Created by bojan on 15/07/2017.
 */

public class UnitsPresenter extends AbstractPresenter implements UnitsContract.Presenter {

    private final UnitsContract.View view;

    private final SettingsRepository settingsRepository;

    public UnitsPresenter(@NonNull final UnitsContract.View view) {
        super(view);

        this.view = view;
        this.settingsRepository = DaggerSettingsRepositoryComponent.builder()
            .localDataSource(new SettingsLocalDataSourceModule())
            .build()
            .inject();
    }

    @Override
    public void saveTemperatureUnit(long settingsId, int unit) {
        compositeDisposable.add(
            settingsRepository
                .saveTemperatureUnit(settingsId, unit)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void savePressureUnit(long settingsId, int unit) {
        compositeDisposable.add(
            settingsRepository
                .savePressureUnit(settingsId, unit)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void saveAccelerationUnit(long settingsId, int unit) {
        compositeDisposable.add(
            settingsRepository
                .saveAccelerationUnit(settingsId, unit)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }
}
