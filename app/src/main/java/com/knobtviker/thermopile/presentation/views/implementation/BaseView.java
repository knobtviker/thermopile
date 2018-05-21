package com.knobtviker.thermopile.presentation.views.implementation;

/**
 * Created by bojan on 15/07/2017.
 */

import android.support.annotation.NonNull;

public interface BaseView {

    void showLoading(final boolean isLoading);

    void showError(@NonNull final Throwable throwable);

}
