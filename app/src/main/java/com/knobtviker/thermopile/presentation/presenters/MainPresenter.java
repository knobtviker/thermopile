package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.di.components.data.DaggerSettingsDataComponent;
import com.knobtviker.thermopile.di.components.data.DaggerThresholdDataComponent;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.presentation.contracts.MainContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 15/07/2017.
 */

public class MainPresenter extends AbstractPresenter implements MainContract.Presenter {

    private final MainContract.View view;

    private SettingsRepository settingsRepository;
    private ThresholdRepository thresholdRepository;

    private RealmResults<Settings> resultsSettings;
    private RealmResults<Threshold> resultsThresholds;

    public MainPresenter(@NonNull final MainContract.View view) {
        super(view);

        this.view = view;
    }

    @Override
    public void subscribe() {
        super.subscribe();

        settingsRepository = DaggerSettingsDataComponent.create().repository();
        thresholdRepository = DaggerThresholdDataComponent.create().repository();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();

        removeListeners();
    }

    @Override
    public void addListeners() {
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
        if (resultsSettings != null && resultsSettings.isValid()) {
            resultsSettings.removeAllChangeListeners();
        }
        if (resultsThresholds != null && resultsThresholds.isValid()) {
            resultsThresholds.removeAllChangeListeners();
        }
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
