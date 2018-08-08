package com.knobtviker.thermopile.shared;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    MessageWhatUser.REGISTER
})
public @interface MessageWhatUser {
    int REGISTER = 0;
}
