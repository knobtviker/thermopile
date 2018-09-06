package com.knobtviker.thermopile.presentation.contracts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Motion;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;
import com.knobtviker.thermopile.presentation.shared.constants.charts.ChartInterval;
import com.knobtviker.thermopile.presentation.shared.constants.charts.ChartType;

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

        void onMotion(@NonNull final List<Motion> data);

        void onMinMax(@StringRes final int labelUnit, @NonNull final String maximum, @NonNull final  String minimum);

        void applyTopPadding(final float maxDataValue, final float maxValue);

        void noScrubbedValue();

        void hasScrubbedValue(@NonNull final String value, @StringRes final int unit, @NonNull final String dateTime);
    }

    interface Presenter extends BasePresenter {

        void settings();

        void data();

        void type(@ChartType final int value);

        void interval(@ChartInterval final int position);

        void scrub(@Nullable final Object value);
    }
}
