package com.knobtviker.thermopile.presentation.utils.constants.time;

import android.support.annotation.IntRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntRange(from = Minutes.MINIMUM, to = Minutes.MAXIMUM)
public @interface Minutes {
    int MINIMUM = 0;
    int MAXIMUM = 59;
}
