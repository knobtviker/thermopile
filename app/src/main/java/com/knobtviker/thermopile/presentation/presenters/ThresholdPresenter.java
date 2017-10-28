package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.presentation.contracts.ThresholdContract;

import io.reactivex.disposables.CompositeDisposable;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by bojan on 29/10/2017.
 */

public class ThresholdPresenter implements ThresholdContract.Presenter {

    private final ThresholdContract.View view;

    private ThresholdRepository thresholdRepository;
    private CompositeDisposable compositeDisposable;

    private RealmResults<Threshold> resultsThresholds;
    private RealmChangeListener<RealmResults<Threshold>> changeListenerThresholds;

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
        if (resultsThresholds != null && resultsThresholds.isValid()) {
            resultsThresholds.removeChangeListener(changeListenerThresholds);
            resultsThresholds = null;
            changeListenerThresholds = null;
        }
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
    public void loadById(long thresholdId) {
        started();

        resultsThresholds = thresholdRepository.loadById(thresholdId);

        if (resultsThresholds != null && resultsThresholds.isValid()) {
            changeListenerThresholds = thresholdsRealmResults -> {
                if (!thresholdsRealmResults.isEmpty()) {
                    view.onThreshold(thresholdsRealmResults.first());
                }
            };
            resultsThresholds.addChangeListener(changeListenerThresholds);
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
