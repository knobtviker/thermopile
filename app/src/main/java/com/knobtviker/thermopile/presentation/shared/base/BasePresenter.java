package com.knobtviker.thermopile.presentation.shared.base;

/**
 * Created by bojan on 15/07/2017.
 */

import android.support.annotation.NonNull;

public interface BasePresenter<V extends BaseView> {

    void dispose();

    void error(@NonNull final Throwable throwable);

    void subscribed();

    void terminated();
}

