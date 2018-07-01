package com.knobtviker.thermopile.presentation.utils.constants;

import android.support.annotation.FloatRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@FloatRange(from = MeasuredAcceleration.MINIMUM, to = MeasuredAcceleration.MAXIMUM)
public @interface MeasuredAcceleration {
    float MINIMUM = 0.0f;
    float MAXIMUM = 2.0f;
}
