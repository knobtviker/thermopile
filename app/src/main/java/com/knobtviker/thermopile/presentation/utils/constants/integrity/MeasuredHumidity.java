package com.knobtviker.thermopile.presentation.utils.constants.integrity;

import android.support.annotation.FloatRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@FloatRange(from = MeasuredHumidity.MINIMUM, to = MeasuredHumidity.MAXIMUM)
public @interface MeasuredHumidity {
    float MINIMUM = 0.0f;
    float MAXIMUM = 100.0f;
}
