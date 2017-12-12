package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.ScreenSaverContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 15/07/2017.
 */

public class ScreenSaverPresenter extends AbstractPresenter implements ScreenSaverContract.Presenter {

    private final ScreenSaverContract.View view;

    private AtmosphereRepository atmosphereRepository;
    private SettingsRepository settingsRepository;

    private RealmResults<Atmosphere> resultsAtmosphere;
    private RealmResults<Settings> resultsSettings;

    public ScreenSaverPresenter(@NonNull final ScreenSaverContract.View view) {
        super(view);

        this.view = view;
    }

    @Override
    public void subscribe() {
        super.subscribe();

        atmosphereRepository = AtmosphereRepository.getInstance();
        settingsRepository = SettingsRepository.getInstance();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();

        removeListeners();

        AtmosphereRepository.destroyInstance();
        SettingsRepository.destroyInstance();
    }

    @Override
    public void addListeners() {
        if (resultsAtmosphere != null && resultsAtmosphere.isValid()) {
            resultsAtmosphere.addChangeListener(atmospheres -> {
                if (!atmospheres.isEmpty()) {
                    final Atmosphere result = atmospheres.first();
                    if (result != null) {
                        view.onDataChanged(result);
                    }
                }
            });
        }
        if (resultsSettings != null && resultsSettings.isValid()) {
            resultsSettings.addChangeListener(settings -> {
                if (!settings.isEmpty()) {
                    final Settings result = settings.first();
                    if (result != null) {
                        view.onSettingsChanged(result);
                    }
                }
            });
        }
    }

    @Override
    public void removeListeners() {
        if (resultsAtmosphere != null && resultsAtmosphere.isValid()) {
            resultsAtmosphere.removeAllChangeListeners();
        }
        if (resultsSettings != null && resultsSettings.isValid()) {
            resultsSettings.removeAllChangeListeners();
        }
    }

    @Override
    public void data(@NonNull final Realm realm) {
        started();

        resultsAtmosphere = atmosphereRepository.latest(realm);

        if (!resultsAtmosphere.isEmpty()) {
            final Atmosphere result = resultsAtmosphere.first();
            if (result != null) {
                view.onDataChanged(result);
            }
        }

        completed();
    }

    @Override
    public void settings(@NonNull final Realm realm) {
        started();

        resultsSettings = settingsRepository.load(realm);

        if (!resultsSettings.isEmpty()) {
            final Settings result = resultsSettings.first();
            if (result != null) {
                view.onSettingsChanged(result);
            }
        }

        completed();
    }
}
