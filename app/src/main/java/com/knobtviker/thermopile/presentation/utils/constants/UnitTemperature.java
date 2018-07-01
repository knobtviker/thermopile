package com.knobtviker.thermopile.presentation.utils.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    UnitTemperature.CELSIUS,
    UnitTemperature.FAHRENHEIT,
    UnitTemperature.KELVIN
})
public @interface UnitTemperature {
    int CELSIUS = 0;
    int FAHRENHEIT = 1;
    int KELVIN = 2;
}
