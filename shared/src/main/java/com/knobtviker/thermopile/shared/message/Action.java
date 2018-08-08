package com.knobtviker.thermopile.shared.message;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    Action.REGISTER,
    Action.CURRENT,
    Action.RESET
})
public @interface Action {
    int REGISTER = -1;
    int CURRENT = -2;
    int RESET = -3;
}
