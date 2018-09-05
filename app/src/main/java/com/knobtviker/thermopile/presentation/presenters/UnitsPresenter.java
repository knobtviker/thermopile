package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.UnitsContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;

import javax.inject.Inject;

import io.reactivex.internal.functions.Functions;

/**
 * Created by bojan on 15/07/2017.
 */

public class UnitsPresenter extends AbstractPresenter<UnitsContract.View> implements UnitsContract.Presenter {

    @NonNull
    private final SettingsRepository settingsRepository;

    @Inject
    public UnitsPresenter(
        @NonNull final UnitsContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final Schedulers schedulers
    ) {
        super(view, schedulers);
        this.settingsRepository = settingsRepository;
    }

    @Override
    public void load() {
        compositeDisposable.add(
            settingsRepository
                .load()
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    view::onLoad,
                    this::error
                )
        );
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
