package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Acceleration;
import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.Altitude;
import com.knobtviker.thermopile.data.models.local.AngularVelocity;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.MagneticField;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

import java.util.List;

/**
 * Created by bojan on 15/07/2017.
 */

public interface ApplicationContract {

    interface View extends BaseView {

        void onTick();

        void showScreensaver();
    }

    interface Presenter extends BasePresenter {

        void tick();

        void createScreensaver();

        void destroyScreensaver();

        void initScreen(final int density, final int rotation);

        void brightness(final int brightness);

        void saveTemperature(@NonNull final List<Temperature> items);

        void savePressure(@NonNull final List<Pressure> items);

        void saveHumidity(@NonNull final List<Humidity> items);

        void saveAltitude(@NonNull final List<Altitude> items);

        void saveAirQuality(@NonNull final List<AirQuality> items);

        void saveAccelerations(@NonNull final List<Acceleration> items);

        void saveAngularVelocities(@NonNull final List<AngularVelocity> items);

        void saveMagneticFields(@NonNull final List<MagneticField> items);
    }
}
