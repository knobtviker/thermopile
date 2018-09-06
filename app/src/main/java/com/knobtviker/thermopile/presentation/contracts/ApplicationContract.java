package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;


/**
 * Created by bojan on 15/07/2017.
 */

public interface ApplicationContract {

    interface View extends BaseView {

        void showScreensaver();
    }

    interface Presenter extends BasePresenter {

        void createScreensaver();

        void destroyScreensaver();

        void saveTemperature(@NonNull final String vendor, @NonNull final String name, final float value);

        void savePressure(@NonNull final String vendor, @NonNull final String name, final float value);

        void saveHumidity(@NonNull final String vendor, @NonNull final String name, final float value);

        void saveAirQuality(@NonNull final String vendor, @NonNull final String name, final float value);

        void saveLuminosity(@NonNull final String vendor, @NonNull final String name, final float value);

        void saveAcceleration(@NonNull final String vendor, @NonNull final String name, final float[] values);

        void saveAngularVelocity(@NonNull final String vendor, @NonNull final String name, final float[] values);

        void saveMagneticField(@NonNull final String vendor, @NonNull final String name, final float[] values);

        void setLastBootTimestamp(final long value);

        void setBootCount(final long value);

        long lastBootTimestamp();

        long bootCount();
    }
}
