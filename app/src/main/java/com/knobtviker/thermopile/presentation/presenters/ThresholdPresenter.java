package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.di.components.data.DaggerThresholdDataComponent;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.presentation.contracts.ThresholdContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 29/10/2017.
 */

public class ThresholdPresenter extends AbstractPresenter implements ThresholdContract.Presenter {

    private final ThresholdContract.View view;

    private ThresholdRepository thresholdRepository;

    private RealmResults<Threshold> resultsThresholds;

    public ThresholdPresenter(@NonNull final ThresholdContract.View view) {
        super(view);

        this.view = view;
    }

    @Override
    public void subscribe() {
        super.subscribe();

        thresholdRepository = DaggerThresholdDataComponent.create().repository();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();

        removeListeners();
    }

    @Override
    public void addListeners() {
        if (resultsThresholds != null && resultsThresholds.isValid()) {
            resultsThresholds.addChangeListener(thresholds -> {
                if (!thresholds.isEmpty()) {
                    final Threshold result = thresholds.first();
                    if (result != null) {
                        view.onThreshold(result);
                    }
                }
            });
        }
    }

    @Override
    public void removeListeners() {
        if (resultsThresholds != null && resultsThresholds.isValid()) {
            resultsThresholds.removeAllChangeListeners();
        }
    }

    @Override
    public void loadById(@NonNull final Realm realm, long thresholdId) {
        started();

        resultsThresholds = thresholdRepository.loadById(realm, thresholdId);

        if (!resultsThresholds.isEmpty()) {
            final Threshold result = resultsThresholds.first();
            if (result != null) {
                view.onThreshold(result);
            }
        }

        completed();
    }

    @Override
    public void save(@NonNull Threshold threshold) {
        started();

        thresholdRepository.save(threshold);

        view.onSaved();
        completed();
    }
}
