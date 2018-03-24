package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.data.DaggerSettingsDataComponent;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.SensorsContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

/**
 * Created by bojan on 15/07/2017.
 */

public class SensorsPresenter extends AbstractPresenter implements SensorsContract.Presenter {

    private final SensorsContract.View view;

    private final SettingsRepository settingsRepository;

    public SensorsPresenter(@NonNull final SensorsContract.View view) {
        super(view);

        this.view = view;
        this.settingsRepository = DaggerSettingsDataComponent.create().repository();
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
}
