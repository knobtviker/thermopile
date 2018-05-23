package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Acceleration;
import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

import java.util.List;

/**
 * Created by bojan on 15/07/2017.
 */

public interface ChartsContract {

    interface View extends BaseView {

        void onTemperature(@NonNull final List<Temperature> data);

        void onHumidity(@NonNull final List<Humidity> data);

        void onPressure(@NonNull final List<Pressure> data);

        void onAirQuality(@NonNull final List<AirQuality> data);

        void onMotion(@NonNull final List<Acceleration> data);
    }

    interface Presenter extends BasePresenter {

        void data(int type, int interval); //TODO: type and interval params
    }
}
