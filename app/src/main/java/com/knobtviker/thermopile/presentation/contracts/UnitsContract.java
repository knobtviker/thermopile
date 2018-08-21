package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitPressure;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;

/**
 * Created by bojan on 15/07/2017.
 */

public interface UnitsContract {

    interface View extends BaseView {

        void onLoad(@NonNull final Settings settings);
    }

    interface Presenter extends BasePresenter {

        void load();

        void saveTemperatureUnit(final long settingsId, @UnitTemperature final int unit);

        void savePressureUnit(final long settingsId, @UnitPressure final int unit);

        void saveAccelerationUnit(final long settingsId, @UnitAcceleration final int unit);
    }
}
