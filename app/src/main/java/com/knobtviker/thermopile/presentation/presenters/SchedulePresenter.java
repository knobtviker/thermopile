package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.domain.repositories.DaggerSettingsRepositoryComponent;
import com.knobtviker.thermopile.di.components.domain.repositories.DaggerThresholdRepositoryComponent;
import com.knobtviker.thermopile.di.modules.data.sources.local.SettingsLocalDataSourceModule;
import com.knobtviker.thermopile.di.modules.data.sources.local.ThresholdLocalDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.presentation.contracts.ScheduleContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import io.reactivex.internal.functions.Functions;

/**
 * Created by bojan on 15/07/2017.
 */

public class SchedulePresenter extends AbstractPresenter implements ScheduleContract.Presenter {

    private final ScheduleContract.View view;

    private final SettingsRepository settingsRepository;
    private final ThresholdRepository thresholdRepository;

    public SchedulePresenter(@NonNull final ScheduleContract.View view) {
        super(view);

        this.view = view;
        this.settingsRepository = DaggerSettingsRepositoryComponent.builder()
            .localDataSource(new SettingsLocalDataSourceModule())
            .build()
            .inject();
        this.thresholdRepository = DaggerThresholdRepositoryComponent.builder()
            .localDataSource(new ThresholdLocalDataSourceModule())
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
    public void thresholds() {
        compositeDisposable.add(
            thresholdRepository
                .load()
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    view::onThresholds,
                    this::error
                )
        );
    }

    @Override
    public void removeThresholdById(long id) {
        compositeDisposable.add(
            thresholdRepository
                .removeById(id)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }
}
