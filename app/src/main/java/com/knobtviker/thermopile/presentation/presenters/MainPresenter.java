package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.presentation.contracts.MainContract;

import io.reactivex.disposables.CompositeDisposable;
import io.realm.Realm;
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
    private RealmResults<Settings> resultsSettings;
    private RealmResults<Threshold> resultsThresholds;

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

        removeListeners();

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
        if (resultsThresholds != null && resultsThresholds.isValid()) {
            resultsThresholds.addChangeListener(thresholds -> {
                if (!thresholds.isEmpty()) {
                    view.onThresholdsChanged(thresholds);
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
        if (resultsThresholds != null && resultsThresholds.isValid()) {
            resultsThresholds.removeAllChangeListeners();
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

    @Override
    public void thresholdsForToday(@NonNull final Realm realm, int day) {
        started();

        //0 = 6
        //1 = 0
        //2 = 1
        //3 = 2
        //4 = 3
        //5 = 4
        //6 = 5
        day = (day == 0 ? 6 : (day - 1));

        resultsThresholds = thresholdRepository.loadByDay(realm, day);

//        if (!resultsThresholds.isEmpty()) {
            view.onThresholdsChanged(resultsThresholds);
//        }

        completed();
    }
}
