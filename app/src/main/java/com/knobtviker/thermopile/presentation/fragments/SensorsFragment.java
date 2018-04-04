package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.presentation.contracts.SensorsContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.SensorsPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.views.adapters.SensorAdapter;
import com.knobtviker.thermopile.presentation.views.communicators.SensorCommunicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.RealmResults;

/**
 * Created by bojan on 15/06/2017.
 */

public class SensorsFragment extends BaseFragment<SensorsContract.Presenter> implements SensorsContract.View, SensorCommunicator {
    public static final String TAG = SensorsFragment.class.getSimpleName();

    private long settingsId = -1L;

    @BindView(R.id.recyclerview_temperature)
    public RecyclerView recyclerViewTemperature;

    @BindView(R.id.recyclerview_pressure)
    public RecyclerView recyclerViewPressure;

    @BindView(R.id.recyclerview_humidity)
    public RecyclerView recyclerViewHumidity;

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

        setupRecyclerViews();

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
        final List<PeripheralDevice> temperatureDevice = new ArrayList<>(0);
        final List<PeripheralDevice> pressureDevice = new ArrayList<>(0);
        final List<PeripheralDevice> humidityDevice = new ArrayList<>(0);
        final List<PeripheralDevice> airQualityDevice = new ArrayList<>(0);
        final List<PeripheralDevice> luminosityDevice = new ArrayList<>(0);
        final List<PeripheralDevice> accelerationDevice = new ArrayList<>(0);
        final List<PeripheralDevice> angularVelocityDevice = new ArrayList<>(0);
        final List<PeripheralDevice> magneticFieldDevice = new ArrayList<>(0);
        sensors
            .forEach(peripheralDevice -> {
                if (peripheralDevice.hasTemperature()) {
                    temperatureDevice.add(peripheralDevice);
                }
                if (peripheralDevice.hasPressure()) {
                    pressureDevice.add(peripheralDevice);
                }
                if (peripheralDevice.hasHumidity()) {
                    humidityDevice.add(peripheralDevice);
                }
                if (peripheralDevice.hasAirQuality()) {
                    airQualityDevice.add(peripheralDevice);
                }
                if (peripheralDevice.hasLuminosity()) {
                    luminosityDevice.add(peripheralDevice);
                }
                if (peripheralDevice.hasAcceleration()) {
                    accelerationDevice.add(peripheralDevice);
                }
                if (peripheralDevice.hasAngularVelocity()) {
                    angularVelocityDevice.add(peripheralDevice);
                }
                if (peripheralDevice.hasMagneticField()) {
                    magneticFieldDevice.add(peripheralDevice);
                }
            });

        //TODO: Extract adapters and notify for changes only if needed.
        if (!temperatureDevice.isEmpty()) {
            recyclerViewTemperature.setAdapter(new SensorAdapter(getContext(), Constants.TYPE_TEMPERATURE, temperatureDevice, this));
        } else {
            //TODO: Empty view showing "No sensors"
        }
        if (!pressureDevice.isEmpty()) {
            recyclerViewPressure.setAdapter(new SensorAdapter(getContext(), Constants.TYPE_PRESSURE, pressureDevice, this));
        } else {
            //TODO: Empty view showing "No sensors"
        }
        if (!humidityDevice.isEmpty()) {
            recyclerViewHumidity.setAdapter(new SensorAdapter(getContext(), Constants.TYPE_HUMIDITY, humidityDevice, this));
        } else {
            //TODO: Empty view showing "No sensors"
        }
    }

    @Override
    public void onSensorChecked(@NonNull String primaryKey, int type, boolean isChecked) {
        presenter.sensorEnabled(realm, primaryKey, type, isChecked);
    }

    private void setupRecyclerViews() {
        recyclerViewTemperature.setFocusable(false);
        recyclerViewTemperature.setNestedScrollingEnabled(false);
        recyclerViewTemperature.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewTemperature.setHasFixedSize(true);
        recyclerViewTemperature.setItemAnimator(null);

        recyclerViewPressure.setFocusable(false);
        recyclerViewPressure.setNestedScrollingEnabled(false);
        recyclerViewPressure.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewPressure.setHasFixedSize(true);
        recyclerViewPressure.setItemAnimator(null);

        recyclerViewHumidity.setFocusable(false);
        recyclerViewHumidity.setNestedScrollingEnabled(false);
        recyclerViewHumidity.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewHumidity.setHasFixedSize(true);
        recyclerViewHumidity.setItemAnimator(null);
    }
}
