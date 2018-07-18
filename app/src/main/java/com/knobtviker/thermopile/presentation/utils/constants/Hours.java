package com.knobtviker.thermopile.presentation.utils.constants;

import android.support.annotation.IntRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntRange(from = Hours.MINIMUM, to = Hours.MAXIMUM)
public @interface Hours {
    int MINIMUM = 0;
    int MAXIMUM = 23;
}
