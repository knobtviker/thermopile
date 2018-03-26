package com.knobtviker.thermopile.presentation.fragments;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.os.Bundle;
import android.os.ParcelUuid;
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

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.sources.raw.EnvironmentProfile;
import com.knobtviker.thermopile.data.sources.raw.GattServerCallback;
import com.knobtviker.thermopile.data.sources.raw.RxBluetoothManager;
import com.knobtviker.thermopile.presentation.contracts.NetworkContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.NetworkPresenter;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by bojan on 15/06/2017.
 */

public class NetworkFragment extends BaseFragment<NetworkContract.Presenter> implements NetworkContract.View, CompoundButton.OnCheckedChangeListener {
    public static final String TAG = NetworkFragment.class.getSimpleName();

    private long settingsId = -1L;

    private RxBluetoothManager rxBluetoothManager;

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

    public static NetworkFragment newInstance() {
        return new NetworkFragment();
    }

    public NetworkFragment() {
        presenter = new NetworkPresenter(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        rxBluetoothManager = RxBluetoothManager.with(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_network, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bind(this, view);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        final boolean hasBluetooth = checkBluetoothSupport();

        setupBluetooth(hasBluetooth);

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
                    rxBluetoothManager.enable();
                } else {
                    if (rxBluetoothManager.isGattServerRunning()) {
                        switchBluetoothGatt.setChecked(false);
                    }
                    if (rxBluetoothManager.isAdvertising()) {
                        switchBluetoothAdvertising.setChecked(false);
                    }
                    rxBluetoothManager.disable();
                }
                break;
            case R.id.switch_bluetooth_gatt:
                if (isChecked) {
                    if (!rxBluetoothManager.isGattServerRunning()) {
                        startGattServer();
                    }
                } else {
                    if (rxBluetoothManager.isGattServerRunning()) {
                        stopGattServer();
                    }
                }
                break;
            case R.id.switch_bluetooth_advertising:
                if (isChecked) {
                    if (!rxBluetoothManager.isAdvertising()) {
                        startAdvertising();
                    }
                } else {
                    if (rxBluetoothManager.isAdvertising()) {
                        stopAdvertising();
                    }
                }
                break;
        }
    }

    private boolean checkBluetoothSupport() {
        return rxBluetoothManager.hasBluetoothLowEnergy();
    }

    @SuppressLint("CheckResult")
    private void setupBluetooth(final boolean hasBluetooth) {
        groupBluetooth.setVisibility(hasBluetooth ? View.VISIBLE : View.GONE);

        if (hasBluetooth) {
            final boolean isEnabled = rxBluetoothManager.isEnabled();
//            rxBluetoothManager.name(getString(R.string.app_name));

            switchBluetoothOnOff.setChecked(isEnabled);
            switchBluetoothOnOff.setText(getString(isEnabled ? R.string.state_on : R.string.state_off));
            switchBluetoothOnOff.setOnCheckedChangeListener(this);

            rxBluetoothManager.state()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    state -> {
                        switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                                setStateOff();
                                break;
                            case BluetoothAdapter.STATE_ON:
                                setStateOn();
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                            case BluetoothAdapter.STATE_TURNING_ON:
                                setStateIndeterminate();
                                break;
                        }
                    }
                );
            switchBluetoothOnOff.setEnabled(true);
            groupGattAdvertising.setVisibility(isEnabled ? View.VISIBLE : View.GONE);

            setupGattServer(isEnabled);
            setupAdvertising(isEnabled);
        }
    }

    private void setupGattServer(final boolean isEnabled) {
        rxBluetoothManager.setDeviceClass(BluetoothClass.Service.INFORMATION, BluetoothClass.Device.COMPUTER_SERVER);
        rxBluetoothManager.setProfile(BluetoothProfile.GATT_SERVER);

        switchBluetoothGatt.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        switchBluetoothGatt.setChecked(rxBluetoothManager.isGattServerRunning());
        switchBluetoothGatt.setOnCheckedChangeListener(this);
        switchBluetoothGatt.setEnabled(true);
    }

    private void setupAdvertising(final boolean isEnabled) {
        switchBluetoothAdvertising.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        switchBluetoothAdvertising.setChecked(rxBluetoothManager.isAdvertising());
        switchBluetoothAdvertising.setOnCheckedChangeListener(this);
        switchBluetoothAdvertising.setEnabled(true);
    }

    private void startGattServer() {
        final GattServerCallback callback = new GattServerCallback(getActivity());
        final BluetoothGattServer gattServer = rxBluetoothManager.startGattServer(callback);
        callback.setGattServer(gattServer);
        if (gattServer != null) {
            //TODO Change and use already adopted convention for ESS and ESP
            gattServer.addService(EnvironmentProfile.createService());
        }
    }

    private void stopGattServer() {
        rxBluetoothManager.stopGattServer();
    }

    private void startAdvertising() {
        rxBluetoothManager.startAdvertising(
            new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build(),
            new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(new ParcelUuid(EnvironmentProfile.UUID_ENVIRONMENT_SERVICE))
                .build(),
            new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    Log.i(TAG, "LE Advertise Started.");
                }

                @Override
                public void onStartFailure(int errorCode) {
                    Log.w(TAG, "LE Advertise Failed: " + errorCode);
                }
            }
        );
    }

    private void stopAdvertising() {
        rxBluetoothManager.stopAdvertising();
    }

    private void setStateOff() {
        setStateSwitchText(getString(R.string.state_off));
        groupGattAdvertising.setVisibility(View.GONE);
    }

    private void setStateOn() {
        setStateSwitchText(getString(R.string.state_on));
        groupGattAdvertising.setVisibility(View.VISIBLE);
    }

    private void setStateIndeterminate() {
        progressBarBluetooth.setVisibility(View.VISIBLE);
        switchBluetoothOnOff.setEnabled(true);
        groupGattAdvertising.setVisibility(View.GONE);
    }

    private void setStateSwitchText(@NonNull final String text) {
        progressBarBluetooth.setVisibility(View.GONE);
        switchBluetoothOnOff.setEnabled(true);
        switchBluetoothOnOff.setText(text);
    }
}
