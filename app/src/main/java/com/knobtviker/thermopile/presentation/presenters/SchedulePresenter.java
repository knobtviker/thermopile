package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.data.DaggerSettingsDataComponent;
import com.knobtviker.thermopile.di.components.data.DaggerThresholdDataComponent;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.presentation.contracts.ScheduleContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

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
        this.settingsRepository = DaggerSettingsDataComponent.create().repository();
        this.thresholdRepository = DaggerThresholdDataComponent.create().repository();
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

    @Override
    public void thresholds() {
        started();

        compositeDisposable.add(
            thresholdRepository
                .load()
                .subscribe(
                    view::onThresholds,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void removeThresholdById(long id) {
        started();

        compositeDisposable.add(
            thresholdRepository
                .removeById(id)
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }
}
