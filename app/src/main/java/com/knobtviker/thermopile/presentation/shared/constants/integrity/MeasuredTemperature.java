package com.knobtviker.thermopile.presentation.shared.constants.integrity;

import android.support.annotation.FloatRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@FloatRange(from = MeasuredTemperature.MINIMUM, to = MeasuredTemperature.MAXIMUM)
public @interface MeasuredTemperature {
    float MINIMUM = 5.0f;
    float MAXIMUM = 35.0f;
}
