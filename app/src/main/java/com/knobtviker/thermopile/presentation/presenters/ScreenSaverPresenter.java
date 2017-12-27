package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.di.components.data.DaggerAtmosphereDataComponent;
import com.knobtviker.thermopile.di.components.data.DaggerSettingsDataComponent;
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

    private RealmResults<Temperature> resultsTemperature;
    private RealmResults<Humidity> resultsHumidity;
    private RealmResults<Pressure> resultsPressure;
    private RealmResults<Settings> resultsSettings;

    public ScreenSaverPresenter(@NonNull final ScreenSaverContract.View view) {
        super(view);

        this.view = view;
    }

    @Override
    public void subscribe() {
        super.subscribe();

        atmosphereRepository = DaggerAtmosphereDataComponent.create().repository();
        settingsRepository = DaggerSettingsDataComponent.create().repository();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();

        removeListeners();
    }

    @Override
    public void addListeners() {
        if (resultsTemperature != null && resultsTemperature.isValid()) {
            resultsTemperature.addChangeListener(temperatures -> {
                if (!temperatures.isEmpty()) {
                    final Temperature result = temperatures.first();
                    if (result != null) {
                        view.onTemperatureChanged(result);
                    }
                }
            });
        }
        if (resultsHumidity != null && resultsHumidity.isValid()) {
            resultsHumidity.addChangeListener(humidities -> {
                if (!humidities.isEmpty()) {
                    final Humidity result = humidities.first();
                    if (result != null) {
                        view.onHumidityChanged(result);
                    }
                }
            });
        }
        if (resultsPressure != null && resultsPressure.isValid()) {
            resultsPressure.addChangeListener(pressures -> {
                if (!pressures.isEmpty()) {
                    final Pressure result = pressures.first();
                    if (result != null) {
                        view.onPressueChanged(result);
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
        if (resultsTemperature != null && resultsTemperature.isValid()) {
            resultsTemperature.removeAllChangeListeners();
        }
        if (resultsHumidity != null && resultsHumidity.isValid()) {
            resultsHumidity.removeAllChangeListeners();
        }
        if (resultsPressure != null && resultsPressure.isValid()) {
            resultsPressure.removeAllChangeListeners();
        }
        if (resultsSettings != null && resultsSettings.isValid()) {
            resultsSettings.removeAllChangeListeners();
        }
    }

    @Override
    public void temperature(@NonNull final Realm realm) {
        started();

        resultsTemperature = atmosphereRepository.latestTemperature(realm);

        if (!resultsTemperature.isEmpty()) {
            final Temperature result = resultsTemperature.first();
            if (result != null) {
                view.onTemperatureChanged(result);
            }
        }

        completed();
    }

    @Override
    public void humidity(@NonNull Realm realm) {
        started();

        resultsHumidity = atmosphereRepository.latestHumidity(realm);

        if (!resultsHumidity.isEmpty()) {
            final Humidity result = resultsHumidity.first();
            if (result != null) {
                view.onHumidityChanged(result);
            }
        }

        completed();
    }

    @Override
    public void pressure(@NonNull Realm realm) {
        started();

        resultsPressure = atmosphereRepository.latestPressure(realm);

        if (!resultsPressure.isEmpty()) {
            final Pressure result = resultsPressure.first();
            if (result != null) {
                view.onPressueChanged(result);
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
