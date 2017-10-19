package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.ScreenSaverContract;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by bojan on 15/07/2017.
 */

public class ScreenSaverPresenter implements ScreenSaverContract.Presenter {

    private final ScreenSaverContract.View view;

    private AtmosphereRepository atmosphereRepository;
    private SettingsRepository settingsRepository;
    private CompositeDisposable compositeDisposable;

    private RealmResults<Atmosphere> resultsAtmosphere;
    private RealmChangeListener<RealmResults<Atmosphere>> changeListenerAtmosphere;

    private RealmResults<Settings> resultsSettings;
    private RealmChangeListener<RealmResults<Settings>> changeListenerSettings;

    public ScreenSaverPresenter(@NonNull final ScreenSaverContract.View view) {
        this.view = view;
    }

    @Override
    public void subscribe() {
        atmosphereRepository = AtmosphereRepository.getInstance();
        settingsRepository = SettingsRepository.getInstance();
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void unsubscribe() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }

        if (resultsAtmosphere != null && resultsAtmosphere.isValid()) {
            resultsAtmosphere.removeChangeListener(changeListenerAtmosphere);
            resultsAtmosphere = null;
            changeListenerAtmosphere = null;
        }

        if (resultsSettings != null && resultsSettings.isValid()) {
            resultsSettings.removeChangeListener(changeListenerSettings);
            resultsSettings = null;
            changeListenerSettings = null;
        }

        AtmosphereRepository.destroyInstance();
        SettingsRepository.destroyInstance();
    }

    @Override
    public void error(@NonNull Throwable throwable) {
        completed();

        view.showError(throwable);
    }

    @Override
    public void started() {
        view.showLoading(true);
    }

    @Override
    public void completed() {
        view.showLoading(false);
    }

    @Override
    public void startClock() {
        compositeDisposable.add(
            Observable
                .interval(1L, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    tick -> view.onClockTick(),
                    this::error,
                    this::completed
                )
        );
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void data() {
        resultsAtmosphere = atmosphereRepository.latest();

        if (resultsAtmosphere != null && resultsAtmosphere.isValid()) {
            changeListenerAtmosphere = atmosphereRealmResults -> {
                if (!atmosphereRealmResults.isEmpty()) {
                    if (!atmosphereRealmResults.isEmpty()) {
                        view.onDataChanged(atmosphereRealmResults.first());
                    }
                }
            };
            resultsAtmosphere.addChangeListener(changeListenerAtmosphere);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void settings() {
        resultsSettings = settingsRepository.load();

        if (resultsSettings != null && resultsSettings.isValid()) {
            changeListenerSettings = settingsRealmResults -> {
                if (!settingsRealmResults.isEmpty()) {
                    if (!settingsRealmResults.isEmpty()) {
                        view.onSettingsChanged(settingsRealmResults.first());
                    }
                }
            };
            resultsSettings.addChangeListener(changeListenerSettings);
        }
    }
}
