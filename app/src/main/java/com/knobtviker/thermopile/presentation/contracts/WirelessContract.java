package com.knobtviker.thermopile.presentation.contracts;

import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;
import com.knobtviker.thermopile.presentation.shared.constants.network.WiFiType;

import java.util.List;

/**
 * Created by bojan on 20/08/2018.
 */

public interface WirelessContract {

    interface View extends BaseView {

        void onHasWifi(final boolean hasWifi);

        void onIsWifiEnabled(final boolean isEnabled);

        void onScanResults(@NonNull final List<ScanResult> scanResults);

        void onConnectWifi(final boolean isConnected);
    }

    interface Presenter extends BasePresenter {

        void hasWifi();

        void isWifiEnabled();

        void enableWifi();

        void scan();

        void observeScanResults();

        void connectWifi(@NonNull final String ssid, @Nullable final String password, @WiFiType final String type);
    }
}
