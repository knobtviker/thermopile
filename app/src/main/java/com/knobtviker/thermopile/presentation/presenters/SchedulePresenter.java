package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.ScheduleContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;

import javax.inject.Inject;

import io.reactivex.internal.functions.Functions;

/**
 * Created by bojan on 15/07/2017.
 */

public class SchedulePresenter extends AbstractPresenter<ScheduleContract.View> implements ScheduleContract.Presenter {

    @NonNull
    private final SettingsRepository settingsRepository;

    @NonNull
    private final ThresholdRepository thresholdRepository;

    @Inject
    public SchedulePresenter(
        @NonNull final ScheduleContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final ThresholdRepository thresholdRepository,
        @NonNull final Schedulers schedulers
    ) {
        super(view, schedulers);
        this.settingsRepository = settingsRepository;
        this.thresholdRepository = thresholdRepository;
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
