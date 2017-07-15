package com.knobtviker.thermopile.presentation.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.contracts.MainContract;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by bojan on 15/07/2017.
 */

public class MainPresenter implements MainContract.Presenter {

    private final Context context;
    private final MainContract.View view;

    //    private UserRepository userRepository;
    private CompositeDisposable compositeDisposable;

    public MainPresenter(@NonNull final Context context, @NonNull final MainContract.View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void subscribe() {
//        userRepository = UserRepository.getInstance(context);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void unsubscribe() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
//        UserRepository.destroyInstance();
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
            Observable.interval(1L, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::onNext,
                    this::error,
                    this::completed
                )
        );
    }

    private void onNext(Long tick) {
        view.onClockTick();
    }
}
