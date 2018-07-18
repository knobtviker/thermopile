package com.knobtviker.thermopile.presentation.utils.constants.time;

import android.support.annotation.IntRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntRange(from = Days.MINIMUM, to = Days.MAXIMUM)
public @interface Days {
    int MINIMUM = 0;
    int MAXIMUM = 6;
}
