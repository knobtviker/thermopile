package com.knobtviker.thermopile.presentation.shared.constants.integrity;

import android.support.annotation.FloatRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@FloatRange(from = MeasuredAirQuality.MINIMUM, to = MeasuredAirQuality.MAXIMUM)
public @interface MeasuredAirQuality {
    float MINIMUM = 0.0f;
    float MAXIMUM = 500.0f;
}
