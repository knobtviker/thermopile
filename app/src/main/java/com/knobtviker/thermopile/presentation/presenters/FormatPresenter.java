package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.components.data.DaggerSettingsDataComponent;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.FormatContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

/**
 * Created by bojan on 15/07/2017.
 */

public class FormatPresenter extends AbstractPresenter implements FormatContract.Presenter {

    private final FormatContract.View view;

    private SettingsRepository settingsRepository;

    public FormatPresenter(@NonNull final FormatContract.View view) {
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
    public void saveFormatDate(long settingsId, @NonNull String item) {
        settingsRepository.saveFormatDate(settingsId, item);
    }

    @Override
    public void saveFormatTime(long settingsId, @NonNull String item) {
        settingsRepository.saveFormatTime(settingsId, item);
    }
}
