package com.knobtviker.thermopile.presentation.utils.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    UnitAcceleration.METERS_PER_SECOND_2,
    UnitAcceleration.G
})
public @interface UnitAcceleration {
    int METERS_PER_SECOND_2 = 0;
    int G = 1;
}
