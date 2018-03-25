package com.knobtviker.thermopile.presentation.fragments;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
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

import com.knobtviker.thermopile.R;
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

    @BindView(R.id.switch_bluetooth_on_off)
    public Switch switchBluetoothOnOff;

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
                    rxBluetoothManager.disable();
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
            switchBluetoothOnOff.setChecked(isEnabled);
            switchBluetoothOnOff.setText(isEnabled ? "On" : "Off");
            switchBluetoothOnOff.setOnCheckedChangeListener(this);

            rxBluetoothManager.state()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    state -> {
                        switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                                progressBarBluetooth.setVisibility(View.GONE);
                                switchBluetoothOnOff.setText("Off");
                                switchBluetoothOnOff.setEnabled(true);
                                break;
                            case BluetoothAdapter.STATE_ON:
                                progressBarBluetooth.setVisibility(View.GONE);
                                switchBluetoothOnOff.setText("On");
                                switchBluetoothOnOff.setEnabled(true);
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                            case BluetoothAdapter.STATE_TURNING_ON:
                                progressBarBluetooth.setVisibility(View.VISIBLE);
                                switchBluetoothOnOff.setEnabled(true);
                                break;
                        }
                    }
                );
            switchBluetoothOnOff.setEnabled(true);
        }
    }
}
