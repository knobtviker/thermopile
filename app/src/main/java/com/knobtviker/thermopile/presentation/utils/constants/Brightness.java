package com.knobtviker.thermopile.presentation.utils.constants;

import android.support.annotation.FloatRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@FloatRange(from = Brightness.MINIMUM, to = Brightness.MAXIMUM)
public @interface Brightness {
    float MINIMUM = 0.1f;
    float MAXIMUM = 1.0f;
}
