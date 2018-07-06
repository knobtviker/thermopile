package com.knobtviker.thermopile.presentation.presenters.implementation;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by bojan on 12/12/2017.
 */

public abstract class AbstractPresenter implements BasePresenter {

    @NonNull
    private final BaseView view;

    @NonNull
    protected final CompositeDisposable compositeDisposable;

    protected AbstractPresenter(@NonNull final BaseView view) {
        this.view = view;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void dispose() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @Override
    public void error(@NonNull Throwable throwable) {
        view.showError(throwable);
    }

    @Override
    public void subscribed() {
        view.showLoading(true);
    }

    @Override
    public void terminated() {
        view.showLoading(false);
    }
}
