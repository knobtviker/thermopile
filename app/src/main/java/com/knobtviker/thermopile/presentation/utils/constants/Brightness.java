package com.knobtviker.thermopile.presentation.utils.constants;

import android.support.annotation.FloatRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@FloatRange(from = Brightness.MINIMUM, to = Brightness.MAXIMUM)
public @interface Brightness {
    float OFF = 0.0f;
    float MINIMUM = 0.05f;
    float MAXIMUM = 1.0f;
}
