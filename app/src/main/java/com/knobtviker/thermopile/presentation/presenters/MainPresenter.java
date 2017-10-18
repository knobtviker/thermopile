package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.presentation.contracts.MainContract;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by bojan on 15/07/2017.
 */

public class MainPresenter implements MainContract.Presenter {

    private final MainContract.View view;


    private ThresholdRepository thresholdRepository;
    private SettingsRepository settingsRepository;
    private CompositeDisposable compositeDisposable;

    public MainPresenter(@NonNull final MainContract.View view) {
        this.view = view;
    }

    @Override
    public void subscribe() {
        thresholdRepository = ThresholdRepository.getInstance();
        settingsRepository = SettingsRepository.getInstance();
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void unsubscribe() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
        ThresholdRepository.destroyInstance();
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
    public void thresholdsForToday(int day) {
        started();

        //0 = 6
        //1 = 0
        //2 = 1
        //3 = 2
        //4 = 3
        //5 = 4
        //6 = 5
        day = (day == 0 ? 6 : (day - 1));

        thresholdRepository.loadByDay(day);
    }

    @Override
    public void settings() {
        settingsRepository.load();
    }
}
