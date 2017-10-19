package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by bojan on 08/08/2017.
 */

public class ApplicationPresenter implements ApplicationContract.Presenter {

    private final ApplicationContract.View view;

    private AtmosphereRepository atmosphereRepository;
    private CompositeDisposable compositeDisposable;

    public ApplicationPresenter(@NonNull final ApplicationContract.View view) {
        this.view = view;
    }

    @Override
    public void subscribe() {
        atmosphereRepository = AtmosphereRepository.getInstance();
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void unsubscribe() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
        AtmosphereRepository.destroyInstance();
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

    @Override
    public void collectData() {
        started();

        compositeDisposable.add(
            Completable.mergeArrayDelayError(
                atmosphereRepository
                    .read()
                    .doOnNext(atmosphereRepository::save)
                    .ignoreElements(),
                atmosphereRepository
                    .luminosity()
                    .doOnNext(view::onLuminosityData)
                    .ignoreElements()
            )
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }
}
