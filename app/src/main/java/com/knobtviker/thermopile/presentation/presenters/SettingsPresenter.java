package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.SettingsContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 15/07/2017.
 */

public class SettingsPresenter extends AbstractPresenter implements SettingsContract.Presenter {

    private final SettingsContract.View view;

    private SettingsRepository settingsRepository;

    private RealmResults<Settings> resultsSettings;

    public SettingsPresenter(@NonNull final SettingsContract.View view) {
        super(view);

        this.view = view;
    }

    @Override
    public void subscribe() {
        super.subscribe();

        settingsRepository = SettingsRepository.getInstance();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();

        removeListeners();

        SettingsRepository.destroyInstance();
    }

    @Override
    public void addListeners() {
        if (resultsSettings != null && resultsSettings.isValid()) {
            resultsSettings.addChangeListener(settings -> {
                if (!settings.isEmpty()) {
                    final Settings result = settings.first();
                    if (result != null) {
                        view.onLoad(result);
                    }
                }
            });
        }
    }

    @Override
    public void removeListeners() {
        if (resultsSettings != null && resultsSettings.isValid()) {
            resultsSettings.removeAllChangeListeners();
        }
    }

    @Override
    public void load(@NonNull final Realm realm) {
        started();

        resultsSettings = settingsRepository.load(realm);

        if (!resultsSettings.isEmpty()) {
            final Settings result = resultsSettings.first();
            if (result != null) {
                view.onLoad(result);
            }
        }

        completed();
    }

    @Override
    public void saveTimezone(long settingsId, @NonNull String timezone) {
        settingsRepository.saveTimezone(settingsId, timezone);
    }

    @Override
    public void saveClockMode(long settingsId, int clockMode) {
        settingsRepository.saveClockMode(settingsId, clockMode);
    }

    @Override
    public void saveFormatDate(long settingsId, @NonNull String item) {
        settingsRepository.saveFormatDate(settingsId, item);
    }

    @Override
    public void saveFormatTime(long settingsId, @NonNull String item) {
        settingsRepository.saveFormatTime(settingsId, item);
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
