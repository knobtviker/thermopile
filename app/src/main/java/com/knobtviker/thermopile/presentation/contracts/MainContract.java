package com.knobtviker.thermopile.presentation.contracts;

import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface MainContract {

    interface View extends BaseView {

        void onClockTick();

    }

    interface Presenter extends BasePresenter {

        void startClock();

    }
}
