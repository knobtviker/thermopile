package com.knobtviker.thermopile.presentation.views.listeners;

import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.shared.constants.network.WiFiType;

public interface WirelessSelectListener {

    void onWirelessSelected(@NonNull final ScanResult scanResult, @WiFiType final String type);
}
