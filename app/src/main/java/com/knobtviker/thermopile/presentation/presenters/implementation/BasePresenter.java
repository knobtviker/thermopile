package com.knobtviker.thermopile.presentation.presenters.implementation;

/**
 * Created by bojan on 15/07/2017.
 */

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

public interface BasePresenter<V extends BaseView> {

    void dispose();

    void error(@NonNull final Throwable throwable);

    void subscribed();

    void terminated();
}

