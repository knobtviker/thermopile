package com.knobtviker.thermopile.presentation.shared.constants.network;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
    WiFiType.WPA2,
    WiFiType.WPA,
    WiFiType.WEP,
    WiFiType.OPEN,
    WiFiType.WPA_EAP,
    WiFiType.IEEE8021X
})
public @interface WiFiType {

    String WPA2 = "WPA2";
    String WPA = "WPA";
    String WEP = "WEP";
    String OPEN = "Open";
    String WPA_EAP = "WPA-EAP";
    String IEEE8021X = "IEEE8021X";
}
