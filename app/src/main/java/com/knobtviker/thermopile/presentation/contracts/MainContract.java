package com.knobtviker.thermopile.presentation.contracts;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
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

        void onThresholdsChanged(@NonNull final List<Threshold> thresholds);
    }

    interface Presenter extends BasePresenter {

        void observeTemperatureChanged(@NonNull final Context context);

        void observePressureChanged(@NonNull final Context context);

        void observeHumidityChanged(@NonNull final Context context);

        void observeAirQualityChanged(@NonNull final Context context);

        void observeAccelerationChanged(@NonNull final Context context);

        void observeDateChanged(@NonNull final Context context);

        void settings();

        void thresholds();
    }
}
