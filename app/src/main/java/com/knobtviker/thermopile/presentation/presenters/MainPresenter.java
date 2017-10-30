package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.presentation.contracts.MainContract;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by bojan on 15/07/2017.
 */

public class MainPresenter implements MainContract.Presenter {

    private final MainContract.View view;

    private AtmosphereRepository atmosphereRepository;
    private SettingsRepository settingsRepository;
    private ThresholdRepository thresholdRepository;
    private CompositeDisposable compositeDisposable;

    private RealmResults<Atmosphere> resultsAtmosphere;
    private RealmChangeListener<RealmResults<Atmosphere>> changeListenerAtmosphere;

    private RealmResults<Settings> resultsSettings;
    private RealmChangeListener<RealmResults<Settings>> changeListenerSettings;

    private RealmResults<Threshold> resultsThresholds;
    private RealmChangeListener<RealmResults<Threshold>> changeListenerThresholds;

    public MainPresenter(@NonNull final MainContract.View view) {
        this.view = view;
    }

    @Override
    public void subscribe() {
        atmosphereRepository = AtmosphereRepository.getInstance();
        settingsRepository = SettingsRepository.getInstance();
        thresholdRepository = ThresholdRepository.getInstance();
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
        if (resultsThresholds != null && resultsThresholds.isValid()) {
            resultsThresholds.removeChangeListener(changeListenerThresholds);
            resultsThresholds = null;
            changeListenerThresholds = null;
        }

        AtmosphereRepository.destroyInstance();
        SettingsRepository.destroyInstance();
        ThresholdRepository.destroyInstance();
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
                    view.onDataChanged(atmosphereRealmResults.first());
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
                    view.onSettingsChanged(settingsRealmResults.first());
                }
            };
            resultsSettings.addChangeListener(changeListenerSettings);
        }
    }

    @Override
    public void thresholdsForToday(int day) {
        //0 = 6
        //1 = 0
        //2 = 1
        //3 = 2
        //4 = 3
        //5 = 4
        //6 = 5
        day = (day == 0 ? 6 : (day - 1));

        resultsThresholds = thresholdRepository.loadByDay(day);

        if (resultsThresholds != null && resultsThresholds.isValid()) {
            changeListenerThresholds = thresholdsRealmResults -> {
                if (!thresholdsRealmResults.isEmpty()) {
                    view.onThresholdsChanged(thresholdsRealmResults);
                }
            };
            resultsThresholds.addChangeListener(changeListenerThresholds);
        }
    }
}
