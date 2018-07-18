package com.knobtviker.thermopile.presentation.utils.constants.integrity;

import android.support.annotation.FloatRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@FloatRange(from = MeasuredAcceleration.MINIMUM, to = MeasuredAcceleration.MAXIMUM)
public @interface MeasuredAcceleration {
    float MINIMUM = 0.0f;
    float MAXIMUM = 19.6133f; // this is 2g (default) same as 19.6133 m/s2
}
