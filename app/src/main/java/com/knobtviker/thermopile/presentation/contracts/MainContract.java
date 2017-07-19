package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.presentation.Reading;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface MainContract {

    interface View extends BaseView {

        void onClockTick();

        void onData(@NonNull final Reading data);
    }

    interface Presenter extends BasePresenter {

        void startClock();

        void data();
    }
}
