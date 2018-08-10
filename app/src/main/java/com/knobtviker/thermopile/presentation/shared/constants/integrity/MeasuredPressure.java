package com.knobtviker.thermopile.presentation.shared.constants.integrity;

import android.support.annotation.FloatRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@FloatRange(from = MeasuredPressure.MINIMUM, to = MeasuredPressure.MAXIMUM)
public @interface MeasuredPressure {
    float MINIMUM = 900.0f;
    float MAXIMUM = 1100.0f;
}
