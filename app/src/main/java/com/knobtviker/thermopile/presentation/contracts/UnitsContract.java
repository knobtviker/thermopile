package com.knobtviker.thermopile.presentation.contracts;

import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.utils.constants.settings.UnitAcceleration;
import com.knobtviker.thermopile.presentation.utils.constants.settings.UnitPressure;
import com.knobtviker.thermopile.presentation.utils.constants.settings.UnitTemperature;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface UnitsContract {

    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter {

        void saveTemperatureUnit(final long settingsId, @UnitTemperature final int unit);

        void savePressureUnit(final long settingsId, @UnitPressure final int unit);

        void saveAccelerationUnit(final long settingsId, @UnitAcceleration final int unit);
    }
}
