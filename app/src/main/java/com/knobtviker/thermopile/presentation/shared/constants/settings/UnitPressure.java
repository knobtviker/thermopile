package com.knobtviker.thermopile.presentation.shared.constants.settings;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    UnitPressure.PASCAL,
    UnitPressure.BAR,
    UnitPressure.PSI
})
public @interface UnitPressure {
    int PASCAL = 0;
    int BAR = 1;
    int PSI = 2;
}
