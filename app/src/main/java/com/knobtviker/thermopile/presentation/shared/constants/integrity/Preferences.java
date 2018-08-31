package com.knobtviker.thermopile.presentation.shared.constants.integrity;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
    Preferences.THEME
})
public @interface Preferences {
    String THEME = "theme";
}
