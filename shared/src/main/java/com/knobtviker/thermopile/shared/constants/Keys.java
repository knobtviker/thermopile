package com.knobtviker.thermopile.shared.constants;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
    Keys.UID,
    Keys.VENDOR,
    Keys.NAME,
    Keys.VALUE,
    Keys.VALUES
})
public @interface Keys {
    String UID = "uid";
    String VENDOR = "vendor";
    String NAME = "name";
    String VALUE = "value";
    String VALUES = "values";
}
