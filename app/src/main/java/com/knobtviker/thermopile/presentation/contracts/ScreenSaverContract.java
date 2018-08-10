package com.knobtviker.thermopile.presentation.contracts;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface ScreenSaverContract {

    interface View extends BaseView {

        void onTemperatureChanged(final float value);

        void onPressureChanged(final float value);

        void onHumidityChanged(final float value);

        void onAirQualityChanged(final float value);

        void onAccelerationChanged(final float[] values);

        void onDateChanged();

        void onSettingsChanged(@NonNull final Settings settings);
    }

    interface Presenter extends BasePresenter {

        void observeDateChanged(@NonNull final Context context);

        void observeTemperature();

        void observePressure();

        void observeHumidity();

        void observeAirQuality();

        void observeAcceleration();

        void settings();
    }
}
