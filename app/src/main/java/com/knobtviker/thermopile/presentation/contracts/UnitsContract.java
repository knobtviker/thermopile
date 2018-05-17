package com.knobtviker.thermopile.presentation.contracts;

import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface UnitsContract {

    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter {

        void saveTemperatureUnit(final long settingsId, final int unit);

        void savePressureUnit(final long settingsId, final int unit);

        void saveMotionUnit(final long settingsId, final int unit);
    }
}
