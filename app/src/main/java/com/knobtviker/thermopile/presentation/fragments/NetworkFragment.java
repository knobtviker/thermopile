package com.knobtviker.thermopile.presentation.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.contracts.NetworkContract;
import com.knobtviker.thermopile.presentation.presenters.NetworkPresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */
//To connect to a Wi‑Fi network via ADB, run `adb shell am startservice -n com.google.wifisetup/.WifiSetupService -a WifiSetupService
// .Connect -e ssid {SSID} -e passphrase {PASSPHRASE}` with the appropriate SSID and passphrase.
public class NetworkFragment extends BaseFragment<NetworkContract.Presenter>
    implements NetworkContract.View, CompoundButton.OnCheckedChangeListener {

    public static final String TAG = NetworkFragment.class.getSimpleName();

    private long settingsId = -1L;

    @Nullable
    private WifiManager wifiManager;

    private boolean hasWiFi = false;

    @BindView(R.id.group_bluetooth)
    public Group groupBluetooth;

    @BindView(R.id.switch_bluetooth_on_off)
    public Switch switchBluetoothOnOff;

    @BindView(R.id.progressbar_bluetooth)
    public ProgressBar progressBarBluetooth;

    @BindView(R.id.group_wifi)
    public Group groupWiFi;

    @BindView(R.id.switch_wifi_on_off)
    public Switch switchWifiOnOff;

    @BindView(R.id.textview_wifi_ssid)
    public TextView textViewWiFiSSID;

    @BindView(R.id.textview_ip_wifi)
    public TextView textViewIpWifi;

    public static NetworkFragment newInstance() {
        return new NetworkFragment();
    }

    public NetworkFragment() {
        // do nothing
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        presenter = new NetworkPresenter(context, this);

        hasWiFi = checkWiFiSupport(context);
        if (hasWiFi) {
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_network, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.hasBluetooth();
    }

    @Override
    public void onResume() {
        presenter.observeTemperature();
        presenter.observePressure();
        presenter.observeHumidity();
        presenter.observeAirQuality();
        presenter.observeAcceleration();

        setupWifi(hasWiFi);

        super.onResume();
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.switch_bluetooth_on_off:
                if (isChecked) {
                    startBluetooth();
                } else {
                    stopBluetooth();
                }
                break;
            case R.id.switch_wifi_on_off:
                if (wifiManager != null) {
                    wifiManager.setWifiEnabled(isChecked);
                }
                break;
        }
    }

    @OnClick({R.id.layout_wifi_connected})
    public void onClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.layout_wifi_connected:
                Timber.i("Open WiFiActivity");
                break;
        }
    }

    @Override
    public void onTemperatureChanged(float value) {

    }

    @Override
    public void onPressureChanged(float value) {

    }

    @Override
    public void onHumidityChanged(float value) {

    }

    @Override
    public void onAirQualityChanged(float value) {

    }

    @Override
    public void onAccelerationChanged(float[] values) {

    }

    @Override
    public void onHasBluetooth(boolean hasBluetooth) {
        groupBluetooth.setVisibility(hasBluetooth ? View.VISIBLE : View.GONE);

        if (hasBluetooth) {
            presenter.isBluetoothEnabled();
            presenter.observeBluetoothState();
        }
    }

    @Override
    public void onIsBluetoothEnabled(boolean isEnabled) {
        switchBluetoothOnOff.setChecked(isEnabled);
        switchBluetoothOnOff.setText(getString(isEnabled ? R.string.state_on : R.string.state_off));
        switchBluetoothOnOff.setEnabled(true);
    }

    @Override
    public void onBluetoothStateIndeterminate() {
        progressBarBluetooth.setVisibility(View.VISIBLE);
        switchBluetoothOnOff.setEnabled(false);
    }

    @Override
    public void onBluetoothState(boolean isOn) {
        progressBarBluetooth.setVisibility(View.GONE);
        switchBluetoothOnOff.setEnabled(true);
        switchBluetoothOnOff.setText(getString(isOn ? R.string.state_on : R.string.state_off));
    }

    private void startBluetooth() {
        presenter.enableBluetooth();
    }

    private void stopBluetooth() {
        presenter.disableBluetooth();
    }

    private boolean checkWiFiSupport(@NonNull final Context context) {
        return context
            .getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_WIFI);
    }

    private void setupWifi(final boolean hasWiFi) {
        groupWiFi.setVisibility(hasWiFi ? View.VISIBLE : View.GONE);

        if (hasWiFi) {
            if (wifiManager != null) {
                final boolean isEnabled = wifiManager.isWifiEnabled();

                switchWifiOnOff.setChecked(isEnabled);
                switchWifiOnOff.setText(getString(isEnabled ? R.string.state_on : R.string.state_off));
                switchWifiOnOff.setOnCheckedChangeListener(this);

                switchWifiOnOff.setEnabled(true);

                final WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                textViewWiFiSSID.setText(wifiInfo.getSSID().replace("\"", ""));
                textViewIpWifi.setText(extractIP(wifiInfo));

                //192.168.1.244 ---
                // SSID: Knobtviker,
                // BSSID: 78:8c:54:33:55:cc,
                // MAC: 02:00:00:00:00:00,
                // Supplicant state: COMPLETED,
                // RSSI: -71,
                // Link speed: 43Mbps,
                // Frequency: 2462MHz,
                // Net ID: 0,
                // Metered hint: false,
                // score: 60

                //Knobtviker       2.4GHz
                //WPA2             43Mbps
                //192.168.1.244    -71
            } else {
                groupWiFi.setVisibility(View.GONE);
            }
        } else {
            groupWiFi.setVisibility(View.GONE);
        }
    }

    private String extractIP(@NonNull final WifiInfo wifiInfo) {
        int ipAddress = wifiInfo.getIpAddress();
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        final byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        try {
            return InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Timber.e(ex);
            return null;
        }
    }
}
