package com.knobtviker.thermopile.presentation.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.data.models.presentation.Threshold;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.presentation.contracts.ScheduleContract;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by bojan on 15/07/2017.
 */

public class SchedulePresenter implements ScheduleContract.Presenter {

    private final Context context;
    private final ScheduleContract.View view;

    private ThresholdRepository thresholdRepository;
    private CompositeDisposable compositeDisposable;

    public SchedulePresenter(@NonNull final Context context, @NonNull final ScheduleContract.View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void subscribe() {
        thresholdRepository = ThresholdRepository.getInstance(context);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void unsubscribe() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable = null;
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
    public void thresholds() {
        started();

        compositeDisposable.add(
          thresholdRepository
            .load()
            .subscribe(
                this::onNext,
                this::error,
                this::completed
            )
        );
    }

    @Override
    public void save(@NonNull Threshold threshold) {
        started();

        compositeDisposable.add(
            thresholdRepository
                .convertToLocal(threshold)
                .flatMap(thresholdTableEntity -> thresholdRepository.save(thresholdTableEntity))
                .subscribe(
                    this::onSavedNext,
                    this::error,
                    this::completed
                )
        );
    }

    private void onNext(@NonNull final ImmutableList<Threshold> thresholds) {
        view.onThresholds(thresholds);
    }

    private void onSavedNext(@NonNull final Threshold threshold) {
        view.onSaved(threshold);
    }
}
