package com.knobtviker.thermopile.presentation.contracts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.knobtviker.thermopile.data.models.presentation.MeasuredData;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;
import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;

import java.util.List;

/**
 * Created by bojan on 15/07/2017.
 */

public interface MainContract {

    interface View extends BaseView {

        void onDateChanged(@NonNull final String date);

        void onZoneAndFormatChanged(
            @NonNull final String timezone,
            final boolean is24HourMode,
            @NonNull @FormatTime final String formatTime
        );

        void onTemperatureUnitChanged(@StringRes final int resId);

        void onPressureUnitChanged(@StringRes final int resId);

        void ontHumidityUnitChanged(@StringRes final int resId);

        void onMotionUnitChanged(@StringRes final int resId);

        void onThresholdUnitAndFormatChanged(
            @UnitTemperature final int unitTemperature,
            @NonNull @FormatTime final String formatTime,
            @NonNull @FormatDate String formatDate
        );

        void onTemperatureChanged(@NonNull final MeasuredData measuredData);

        void onPressureChanged(@NonNull final MeasuredData measuredData);

        void onHumidityChanged(@NonNull final MeasuredData measuredData);

        void onAirQualityChanged(@NonNull final MeasuredData measuredData);

        void onAccelerationChanged(@NonNull final MeasuredData measuredData);

        void onThresholdsChanged(@NonNull final List<ThresholdInterval> thresholds);
    }

    interface Presenter extends BasePresenter {

        void observeDateChanged(@NonNull final Context context);

        void observeAtmosphere();
    }
}
