package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.presentation.contracts.ThresholdContract;

import io.reactivex.disposables.CompositeDisposable;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 29/10/2017.
 */

public class ThresholdPresenter implements ThresholdContract.Presenter {

    private final ThresholdContract.View view;

    private ThresholdRepository thresholdRepository;
    private CompositeDisposable compositeDisposable;

    private RealmResults<Threshold> resultsThresholds;

    public ThresholdPresenter(@NonNull final ThresholdContract.View view) {
        this.view = view;
    }

    @Override
    public void subscribe() {
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
