package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.ScreenSaverContract;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by bojan on 15/07/2017.
 */

public class ScreenSaverPresenter implements ScreenSaverContract.Presenter {

    private final ScreenSaverContract.View view;

    private AtmosphereRepository atmosphereRepository;
    private SettingsRepository settingsRepository;
    private CompositeDisposable compositeDisposable;

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

//    @Override
//    public void startClock() {
//        compositeDisposable.add(
//            Observable.interval(1L, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    tick -> onNext(),
//                    this::error,
//                    this::completed
//                )
//        );
//    }

//    @Override
//    public void data() {
//        started();
//
//        compositeDisposable.add(
//            atmosphereRepository
//                .last()
//                .subscribe(
//                    this::onDataNext,
//                    this::error,
//                    this::completed
//                )
//        );
//    }

    @Override
    public void settings() {
        settingsRepository.load();
    }
}
