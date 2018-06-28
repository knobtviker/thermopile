package com.knobtviker.thermopile.presentation.contracts;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

import java.util.List;

/**
 * Created by bojan on 15/07/2017.
 */

public interface MainContract {

    interface View extends BaseView {

        void onTemperatureChanged(final float value);

        void onPressureChanged(final float value);

        void onHumidityChanged(final float value);

        void onAirQualityChanged(final float value);

        void onAccelerationChanged(final float[] values);

        void onDateChanged();

        void onSettingsChanged(@NonNull final Settings settings);

        void onThresholdsChanged(@NonNull final List<ThresholdInterval> thresholds);
    }

    interface Presenter extends BasePresenter {

        void observeDateChanged(@NonNull final Context context);

        void observeTemperature();

        void observePressure();

        void observeHumidity();

        void observeAirQuality();

        void observeAcceleration();

        void settings();

        void thresholds();
    }
}
