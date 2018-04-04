package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.presentation.contracts.SensorsContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.SensorsPresenter;

import io.realm.RealmResults;

/**
 * Created by bojan on 15/06/2017.
 */

public class SensorsFragment extends BaseFragment<SensorsContract.Presenter> implements SensorsContract.View {
    public static final String TAG = SensorsFragment.class.getSimpleName();

    private long settingsId = -1L;

    public static SensorsFragment newInstance() {
        return new SensorsFragment();
    }

    public SensorsFragment() {
        presenter = new SensorsPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_sensors, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.sensors(realm);
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    @Override
    public void onSensors(@NonNull RealmResults<PeripheralDevice> sensors) {
        sensors
            .forEach(peripheralDevice -> {
                Log.i(TAG, peripheralDevice.vendor()+" "+peripheralDevice.name()+" connected: "+peripheralDevice.connected()+" enabled: "+peripheralDevice.enabled());
            });
    }
}
