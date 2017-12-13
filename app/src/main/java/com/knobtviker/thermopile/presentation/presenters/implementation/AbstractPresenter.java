package com.knobtviker.thermopile.presentation.presenters.implementation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by bojan on 12/12/2017.
 */

public abstract class AbstractPresenter implements BasePresenter {

    @NonNull
    protected final BaseView view;

    @Nullable
    protected CompositeDisposable compositeDisposable;

    public AbstractPresenter(@NonNull final BaseView view) {
        this.view = view;
    }

    @Override
    public void subscribe() {
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void unsubscribe() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
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
}
