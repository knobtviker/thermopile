package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.data.DaggerSettingsDataComponent;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.LocaleContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

/**
 * Created by bojan on 15/07/2017.
 */

public class LocalePresenter extends AbstractPresenter implements LocaleContract.Presenter {

    private final LocaleContract.View view;

    private final SettingsRepository settingsRepository;

    public LocalePresenter(@NonNull final LocaleContract.View view) {
        super(view);

        this.view = view;
        this.settingsRepository = DaggerSettingsDataComponent.create().repository();
    }

    @Override
    public void saveTimezone(long settingsId, @NonNull String timezone) {
        settingsRepository.saveTimezone(settingsId, timezone);
    }

    @Override
    public void saveClockMode(long settingsId, int clockMode) {
        settingsRepository.saveClockMode(settingsId, clockMode);
    }
}
