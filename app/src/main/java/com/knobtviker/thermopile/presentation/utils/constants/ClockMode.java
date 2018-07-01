package com.knobtviker.thermopile.presentation.utils.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    ClockMode._12H,
    ClockMode._24H
})
public @interface ClockMode {
    int _12H = 0;
    int _24H = 1;
}
