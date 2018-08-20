package com.knobtviker.thermopile.presentation.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.util.Pair;
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
import com.knobtviker.thermopile.presentation.shared.constants.integrity.RequestCode;

import androidx.navigation.fragment.NavHostFragment;
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
        presenter.hasWifi();
    }

    @Override
    public void onResume() {
        presenter.observeTemperature();
        presenter.observePressure();
        presenter.observeHumidity();
        presenter.observeAirQuality();
        presenter.observeAcceleration();

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
                    presenter.enableBluetooth();
                } else {
                    presenter.disableBluetooth();
                }
                break;
            case R.id.switch_wifi_on_off:
                if (isChecked) {
                    presenter.enableWifi();
                } else {
                    presenter.disableWifi();
                }
                break;
        }
    }

    @OnClick({R.id.layout_wifi_connected})
    public void onClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.layout_wifi_connected:
                NavHostFragment
                    .findNavController(this)
                    .navigate(R.id.showWirelessAction);
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
    }

    @Override
    public void onIsBluetoothEnabled(boolean isEnabled) {
        switchBluetoothOnOff.setChecked(isEnabled);
        switchBluetoothOnOff.setText(getString(isEnabled ? R.string.state_on : R.string.state_off));
        switchBluetoothOnOff.setEnabled(true);
        switchBluetoothOnOff.setOnCheckedChangeListener(this);
    }

    @Override
    public void onBluetoothState(boolean isOn) {
        progressBarBluetooth.setVisibility(View.GONE);
        switchBluetoothOnOff.setEnabled(true);
        switchBluetoothOnOff.setText(getString(isOn ? R.string.state_on : R.string.state_off));
    }

    @Override
    public void onBluetoothStateIndeterminate() {
        progressBarBluetooth.setVisibility(View.VISIBLE);
        switchBluetoothOnOff.setEnabled(false);
    }

    @Override
    public void onBluetoothSetupCompleted() {
        presenter.enableDiscoverability(requireActivity(), RequestCode.BLUETOOTH_DISCOVERABILITY);
    }

    @Override
    public void onHasWifi(boolean hasWifi) {
        groupWiFi.setVisibility(hasWifi ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onIsWifiEnabled(boolean isEnabled) {
        switchWifiOnOff.setChecked(isEnabled);
        switchWifiOnOff.setText(getString(isEnabled ? R.string.state_on : R.string.state_off));
        switchWifiOnOff.setOnCheckedChangeListener(this);

        switchWifiOnOff.setEnabled(true);
    }

    @Override
    public void onWifiInfo(@NonNull Pair<String, String> ssidIpPair) {
        textViewWiFiSSID.setText(ssidIpPair.first);
        textViewIpWifi.setText(ssidIpPair.second);
    }
}
