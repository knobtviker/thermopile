package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.data.DaggerSettingsDataComponent;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.UnitContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

/**
 * Created by bojan on 15/07/2017.
 */

public class UnitPresenter extends AbstractPresenter implements UnitContract.Presenter {

    private final UnitContract.View view;

    private SettingsRepository settingsRepository;

    public UnitPresenter(@NonNull final UnitContract.View view) {
        super(view);

        this.view = view;
    }

    @Override
    public void subscribe() {
        super.subscribe();

        settingsRepository = DaggerSettingsDataComponent.create().repository();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();

        removeListeners();
    }

    @Override
    public void addListeners() {

    }

    @Override
    public void removeListeners() {

    }

    @Override
    public void saveTemperatureUnit(long settingsId, int unit) {
        settingsRepository.saveTemperatureUnit(settingsId, unit);
    }

    @Override
    public void savePressureUnit(long settingsId, int unit) {
        settingsRepository.savePressureUnit(settingsId, unit);
    }
}
