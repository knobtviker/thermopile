package com.knobtviker.thermopile.presentation.utils.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    RequestCode.BLUETOOTH_DISCOVERABILITY
})
public @interface RequestCode {
    int BLUETOOTH_DISCOVERABILITY = 8008;
}
