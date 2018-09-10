package com.knobtviker.thermopile.presentation.shared.base;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.schedulers.Schedulers;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by bojan on 12/12/2017.
 */

public abstract class AbstractPresenter<V extends BaseView> implements BasePresenter {

    @NonNull
    protected V view;

    @NonNull
    protected Schedulers schedulers;

    @NonNull
    protected final CompositeDisposable compositeDisposable;

    protected AbstractPresenter(@NonNull final V view, @NonNull final Schedulers schedulers) {
        this.view = view;
        this.schedulers = schedulers;
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
