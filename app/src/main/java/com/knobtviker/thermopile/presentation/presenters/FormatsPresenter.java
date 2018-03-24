package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.data.DaggerSettingsDataComponent;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.FormatsContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

/**
 * Created by bojan on 15/07/2017.
 */

public class FormatsPresenter extends AbstractPresenter implements FormatsContract.Presenter {

    private final FormatsContract.View view;

    private final SettingsRepository settingsRepository;

    public FormatsPresenter(@NonNull final FormatsContract.View view) {
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

    @Override
    public void saveFormatDate(long settingsId, @NonNull String item) {
        settingsRepository.saveFormatDate(settingsId, item);
    }

    @Override
    public void saveFormatTime(long settingsId, @NonNull String item) {
        settingsRepository.saveFormatTime(settingsId, item);
    }
}
