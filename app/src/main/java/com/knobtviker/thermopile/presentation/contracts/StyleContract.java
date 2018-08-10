package com.knobtviker.thermopile.presentation.contracts;

import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;

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
