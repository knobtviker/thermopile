package com.knobtviker.thermopile.shared.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    Uid.INVALID,
    Uid.DRIVERS,
    Uid.FRAM,
    Uid.BLUETOOTH
})
public @interface Uid {
    int INVALID = -1;
    int DRIVERS = 0;
    int FRAM = 1;
    int BLUETOOTH = 2;
}
