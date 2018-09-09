package com.knobtviker.thermopile.presentation.contracts;

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

        void onCelsiusChecked();

        void onFahrenheitChecked();

        void onKelvinChecked();

        void onPascalChecked();

        void onBarChecked();

        void onPsiChecked();

        void onMs2Checked();

        void onGChecked();

        void onCms2Checked();
        
        void onGalChecked();
    }

    interface Presenter extends BasePresenter {

        void load();

        void saveTemperatureUnit(@UnitTemperature final int unit);

        void savePressureUnit(@UnitPressure final int unit);

        void saveAccelerationUnit(@UnitAcceleration final int unit);
    }
}
