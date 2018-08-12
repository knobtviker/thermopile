package com.knobtviker.thermopile.shared.message;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    Action.REGISTER,
    Action.CURRENT,
    Action.RESET,
    Action.LAST_BOOT_TIMESTAMP,
    Action.BOOT_COUNT
})
public @interface Action {
    int REGISTER = -1;
    int CURRENT = -2;
    int RESET = -3;
    int LAST_BOOT_TIMESTAMP = -4;
    int BOOT_COUNT = -5;
}
