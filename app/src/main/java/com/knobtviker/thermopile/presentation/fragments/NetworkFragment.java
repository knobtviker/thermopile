package com.knobtviker.thermopile.presentation.fragments;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.sources.raw.EnvironmentProfile;
import com.knobtviker.thermopile.data.sources.raw.GattServerCallback;
import com.knobtviker.thermopile.presentation.contracts.NetworkContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.NetworkPresenter;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by bojan on 15/06/2017.
 */

public class NetworkFragment extends BaseFragment<NetworkContract.Presenter> implements NetworkContract.View, CompoundButton.OnCheckedChangeListener {
    public static final String TAG = NetworkFragment.class.getSimpleName();

    private long settingsId = -1L;

    @Nullable
    private WifiManager wifiManager;

    private boolean hasWiFi = false;

    @BindView(R.id.group_bluetooth)
    public Group groupBluetooth;

    @BindView(R.id.group_gatt_advertising)
    public Group groupGattAdvertising;

    @BindView(R.id.switch_bluetooth_on_off)
    public Switch switchBluetoothOnOff;

    @BindView(R.id.switch_bluetooth_gatt)
    public Switch switchBluetoothGatt;

    @BindView(R.id.switch_bluetooth_advertising)
    public Switch switchBluetoothAdvertising;

    @BindView(R.id.progressbar_bluetooth)
    public ProgressBar progressBarBluetooth;

    @BindView(R.id.group_wifi)
    public Group groupWiFi;

    @BindView(R.id.switch_wifi_on_off)
    public Switch switchWifiOnOff;

    @BindView(R.id.textview_wifi_ssid)
    public TextView textViewWiFiSSID;

    @BindView(R.id.textview_ip)
    public TextView textViewIP;

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
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_network, container, false);
    }

    @Override
    public void onResume() {
        presenter.hasBluetooth();


        setupWifi(hasWiFi);

        super.onResume();
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.switch_bluetooth_on_off:
                if (isChecked) {
                    presenter.bluetooth(isChecked);
                } else {
                    if (presenter.isGattServerRunning()) {
                        switchBluetoothGatt.setChecked(false);
                    }
                    if (presenter.isBluetoothAdvertising()) {
                        switchBluetoothAdvertising.setChecked(false);
                    }
                    presenter.bluetooth(isChecked);
                }
                break;
            case R.id.switch_bluetooth_gatt:
                if (isChecked) {
                    if (!presenter.isGattServerRunning()) {
                        startGattServer();
                    }
                } else {
                    if (presenter.isGattServerRunning()) {
                        stopGattServer();
                    }
                }
                break;
            case R.id.switch_bluetooth_advertising:
                if (isChecked) {
                    if (!presenter.isBluetoothAdvertising()) {
                        startAdvertising();
                    }
                } else {
                    if (presenter.isBluetoothAdvertising()) {
                        stopAdvertising();
                    }
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
                Log.i(TAG, "Open WiFiActivity");
                break;
        }
    }

    @Override
    public void onHasBluetooth(boolean hasBluetooth) {
        setupBluetooth(hasBluetooth);
    }

    @Override
    public void onBluetoothEnabled(boolean isEnabled) {
        switchBluetoothOnOff.setChecked(isEnabled);
        switchBluetoothOnOff.setText(getString(isEnabled ? R.string.state_on : R.string.state_off));
        switchBluetoothOnOff.setOnCheckedChangeListener(this);

        presenter.observeBluetoothState();

        switchBluetoothOnOff.setEnabled(true);
        groupGattAdvertising.setVisibility(isEnabled ? View.VISIBLE : View.GONE);

        setupGattServer(isEnabled);
        setupAdvertising(isEnabled);
    }

    @Override
    public void onBluetoothStateIndeterminate() {
        progressBarBluetooth.setVisibility(View.VISIBLE);
        switchBluetoothOnOff.setEnabled(true);
        groupGattAdvertising.setVisibility(View.GONE);
    }

    @Override
    public void onBluetoothState(boolean isOn) {
        progressBarBluetooth.setVisibility(View.GONE);
        switchBluetoothOnOff.setEnabled(true);
        switchBluetoothOnOff.setText(getString(isOn ? R.string.state_on : R.string.state_off));
        groupGattAdvertising.setVisibility(isOn ? View.VISIBLE : View.GONE);
    }

    @SuppressLint("CheckResult")
    private void setupBluetooth(final boolean hasBluetooth) {
        groupBluetooth.setVisibility(hasBluetooth ? View.VISIBLE : View.GONE);

        if (hasBluetooth) {
            presenter.isBluetoothEnabled();
        }
    }

    private void setupGattServer(final boolean isEnabled) {
        presenter.setBluetoothDeviceClass(BluetoothClass.Service.INFORMATION, BluetoothClass.Device.COMPUTER_SERVER);
        presenter.setBluetoothProfile(BluetoothProfile.GATT_SERVER);

        switchBluetoothGatt.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        switchBluetoothGatt.setChecked(presenter.isGattServerRunning());
        switchBluetoothGatt.setOnCheckedChangeListener(this);
        switchBluetoothGatt.setEnabled(true);
    }

    private void setupAdvertising(final boolean isEnabled) {
        switchBluetoothAdvertising.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        switchBluetoothAdvertising.setChecked(presenter.isBluetoothAdvertising());
        switchBluetoothAdvertising.setOnCheckedChangeListener(this);
        switchBluetoothAdvertising.setEnabled(true);
    }

    private void startGattServer() {
        final GattServerCallback callback = new GattServerCallback(getActivity());
        final BluetoothGattServer gattServer = presenter.startGattServer(callback);
        callback.setGattServer(gattServer);
        if (gattServer != null) {
            //TODO Change and use already adopted convention for ESS and ESP
            gattServer.addService(EnvironmentProfile.createService());
        }
    }

    private void stopGattServer() {
        presenter.stopGattServer();
    }

    private void startAdvertising() {
        presenter.startBluetoothAdvertising();
    }

    private void stopAdvertising() {
        presenter.stopBluetoothAdvertising();
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
                textViewIP.setText(extractIP(wifiInfo));

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
            Log.e(TAG, ex.getMessage(), ex);
            return null;
        }
    }
}
