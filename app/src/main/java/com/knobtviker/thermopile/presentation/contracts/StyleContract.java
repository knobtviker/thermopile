package com.knobtviker.thermopile.presentation.contracts;

import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface StyleContract {

    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter {

        void saveTheme(final long settingsId, final int value);

        void saveScreensaverTimeout(final long settingsId, final int value);
    }
}
