package com.knobtviker.thermopile.presentation.utils.constants.settings;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
    FormatDay.EEEE,
    FormatDay.EE
})
public @interface FormatDay {

    String EEEE = "EEEE";
    String EE = "EE";
}
