package com.knobtviker.thermopile.shared.message;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    Data.LAST_BOOT_TIMESTAMP,
    Data.BOOT_COUNT
})
public @interface Data {
    int LAST_BOOT_TIMESTAMP = 0;
    int BOOT_COUNT = 1;
}
