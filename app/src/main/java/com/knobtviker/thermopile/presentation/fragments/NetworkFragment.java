package com.knobtviker.thermopile.presentation.fragments;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.ParcelUuid;
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

import com.google.android.things.bluetooth.BluetoothConfigManager;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.sources.raw.EnvironmentProfile;
import com.knobtviker.thermopile.data.sources.raw.GattServerCallback;
import com.knobtviker.thermopile.data.sources.raw.RxBluetoothManager;
import com.knobtviker.thermopile.data.sources.raw.TimeProfile;
import com.knobtviker.thermopile.presentation.contracts.NetworkContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.NetworkPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.views.listeners.GattServerListener;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class NetworkFragment extends BaseFragment<NetworkContract.Presenter> implements NetworkContract.View, CompoundButton.OnCheckedChangeListener, GattServerListener {
    public static final String TAG = NetworkFragment.class.getSimpleName();

    private long settingsId = -1L;

    @Nullable
    private BluetoothGattServer gattServer;

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
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_network, container, false);
    }

    @Override
    public void onResume() {
        presenter.observeTemperatureChanged(getContext());
        presenter.observePressureChanged(getContext());
        presenter.observeHumidityChanged(getContext());
        presenter.observeAirQualityChanged(getContext());
        presenter.observeAccelerationChanged(getContext());
        presenter.hasBluetooth();

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
                presenter.bluetooth(isChecked);
                break;
            case R.id.switch_bluetooth_gatt:
                if (isChecked) {
                    startGattServer();
                } else {
                    stopGattServer();
                }
                break;
            case R.id.switch_bluetooth_advertising:
                if (isChecked) {
                    startAdvertising();
                } else {
                    stopAdvertising();
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
        setupBluetooth(hasBluetooth);
    }

    @Override
    public void onBluetoothEnabled(boolean isEnabled) {
        switchBluetoothOnOff.setChecked(isEnabled);
        switchBluetoothOnOff.setText(getString(isEnabled ? R.string.state_on : R.string.state_off));

        switchBluetoothOnOff.setEnabled(true);

        groupGattAdvertising.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
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

        setupGattServer(isOn);
        setupAdvertising(isOn);

        if (isOn) {
            presenter.name(getString(R.string.app_name));
            presenter.discoverable(getActivity(), Constants.REQUEST_CODE_BLUETOOTH_DISCOVERABILITY, RxBluetoothManager.MAX_DISCOVERABILITY_PERIOD_SECONDS);
        }
    }

    @Override
    public void onCheckGattServer(boolean isGattServerRunning) {
        switchBluetoothGatt.setChecked(isGattServerRunning);
        switchBluetoothGatt.setOnCheckedChangeListener(this);
        switchBluetoothGatt.setEnabled(true);
    }

    @Override
    public void onGattServerStarted(@NonNull BluetoothGattServer gattServer) {
        this.gattServer = gattServer;

        //TODO: Finish ESS and ESP BLE profile and service
        final EnvironmentProfile environmentProfile = new EnvironmentProfile();
        final TimeProfile timeProfile = new TimeProfile();

        this.gattServer.addService(environmentProfile.createService());
        this.gattServer.addService(timeProfile.createService());
    }

    @Override
    public void onCheckBluetoothAdvertising(boolean isAdvertising) {
        switchBluetoothAdvertising.setChecked(isAdvertising);
        switchBluetoothAdvertising.setOnCheckedChangeListener(this);
        switchBluetoothAdvertising.setEnabled(true);
    }

    @Override
    public void onGattConnectionStateChange(@NonNull BluetoothDevice device, int status, int newState) {
        Timber.i(String.format("onGattConnectionStateChange: %s status: %d newState: %d", device.toString(), status, newState));
    }

    @Override
    public void onGattSendResponse(@NonNull BluetoothDevice device, int requestId, int status, @NonNull UUID uuid) {
        if (gattServer != null) {
            byte[] response = new byte[0];
//            if (uuid.equals(EnvironmentProfile.UUID_TEMPERATURE)) {
//                response = EnvironmentProfile.toByteArray(Math.round(atmosphere.temperature()));
//                Log.i(TAG, atmosphere.temperature() + "");
//            } else if (uuid.equals(EnvironmentProfile.UUID_PRESSURE)) {
//                response = EnvironmentProfile.toByteArray(atmosphere.pressure());
//                Log.i(TAG, atmosphere.pressure() + "");
//            } else if (uuid.equals(EnvironmentProfile.UUID_HUMIDITY)) {
//                response = EnvironmentProfile.toByteArray(atmosphere.humidity());
//                Log.i(TAG, atmosphere.humidity() + "");
        } else {
            Timber.e("Invalid Characteristic Read: %s", uuid);
            gattServer.sendResponse(device, requestId, status, 0, null);
        }

//            gattServer.sendResponse(device, requestId, status, 0, response);
//        }
    }

    @Override
    public void onGattDescriptorResponse(@NonNull BluetoothDevice device, int requestId, int status, int offset, byte[] value) {
        if (gattServer != null) {
            gattServer.sendResponse(device, requestId, status, offset, value);
        }
    }

    @SuppressLint("CheckResult")
    private void setupBluetooth(final boolean hasBluetooth) {
        groupBluetooth.setVisibility(hasBluetooth ? View.VISIBLE : View.GONE);

        if (hasBluetooth) {
            switchBluetoothOnOff.setOnCheckedChangeListener(this);
            presenter.isBluetoothEnabled();
            presenter.observeBluetoothState();
        }
    }

    private void setupGattServer(final boolean isOn) {
        switchBluetoothGatt.setVisibility(isOn ? View.VISIBLE : View.GONE);

        if (isOn) {
            presenter.setBluetoothDeviceClass(BluetoothClass.Service.INFORMATION, BluetoothClass.Device.COMPUTER_SERVER, BluetoothConfigManager.IO_CAPABILITY_IO
            );
            presenter.setBluetoothProfiles(Collections.singletonList(BluetoothProfile.GATT_SERVER));

            switchBluetoothGatt.setEnabled(false);
            presenter.isGattServerRunning();
        } else {
            gattServer = null;
            onCheckGattServer(false);
        }
    }

    private void setupAdvertising(final boolean isOn) {
        switchBluetoothAdvertising.setVisibility(isOn ? View.VISIBLE : View.GONE);
        switchBluetoothAdvertising.setEnabled(false);

        if (isOn) {
            presenter.isBluetoothAdvertising();
        } else {
            onCheckBluetoothAdvertising(false);
        }
    }

    private void startGattServer() {
        presenter.startGattServer(GattServerCallback.create(this));
    }

    private void stopGattServer() {
        presenter.stopGattServer();
    }

    private void startAdvertising() {
        presenter.startBluetoothAdvertising(
            new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .build(),
            new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(new ParcelUuid(TimeProfile.TIME_SERVICE))
                .addServiceUuid(new ParcelUuid(EnvironmentProfile.ENVIRONMENT_SENSING_SERVICE))
                .build(),
            new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    Timber.i("LE Advertise Started: " + settingsInEffect.toString());
                }

                @Override
                public void onStartFailure(int errorCode) {
                    Timber.e( "LE Advertise Failed: " + errorCode);
                }
            }
        );
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
