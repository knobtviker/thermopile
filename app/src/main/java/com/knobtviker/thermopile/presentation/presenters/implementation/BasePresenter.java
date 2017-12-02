package com.knobtviker.thermopile.presentation.presenters.implementation;

/**
 * Created by bojan on 15/07/2017.
 */

import android.support.annotation.NonNull;

public interface BasePresenter {

    void subscribe();

    void unsubscribe();

    void error(@NonNull final Throwable throwable);

    void started();

    void completed();

    void addListeners();

    void removeListeners();
}

