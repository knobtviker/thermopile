package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import butterknife.BindViews;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class SensorsFragment extends BaseFragment<SensorsContract.Presenter> implements SensorsContract.View, SensorCommunicator {
    public static final String TAG = SensorsFragment.class.getSimpleName();

    private long settingsId = -1L;

    @BindViews({R.id.recyclerview_temperature, R.id.recyclerview_pressure, R.id.recyclerview_humidity, R.id.recyclerview_iaq, R.id.recyclerview_luminosity, R.id.recyclerview_acceleration, R.id.recyclerview_angular_velocity, R.id.recyclerview_magnetic_field})
    public List<RecyclerView> recyclerViews;

    private SensorAdapter temperatureAdapter;
    private SensorAdapter pressureAdapter;
    private SensorAdapter humidityAdapter;
    private SensorAdapter airqualityAdapter;
    private SensorAdapter luminosityAdapter;
    private SensorAdapter accelerationAdapter;
    private SensorAdapter angularVelocityAdapter;
    private SensorAdapter magneticFieldAdapter;

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
        Timber.e(throwable);
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

        temperatureAdapter.updateData(temperatureDevice);
        pressureAdapter.updateData(pressureDevice);
        humidityAdapter.updateData(humidityDevice);
        airqualityAdapter.updateData(airQualityDevice);
        luminosityAdapter.updateData(luminosityDevice);
        accelerationAdapter.updateData(accelerationDevice);
        angularVelocityAdapter.updateData(angularVelocityDevice);
        magneticFieldAdapter.updateData(magneticFieldDevice);
    }

    @Override
    public void onSensorChecked(@NonNull String primaryKey, int type, boolean isChecked) {
        presenter.sensorEnabled(realm, primaryKey, type, isChecked);
    }

    private void setupRecyclerViews() {
        recyclerViews.forEach(
            recyclerView -> {
                recyclerView.setFocusable(false);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                recyclerView.setHasFixedSize(true);
                switch (recyclerView.getId()) {
                    case R.id.recyclerview_temperature:
                        temperatureAdapter = new SensorAdapter(getContext(), Constants.TYPE_TEMPERATURE, this);
                        recyclerView.setAdapter(temperatureAdapter);
                        break;
                    case R.id.recyclerview_pressure:
                        pressureAdapter = new SensorAdapter(getContext(), Constants.TYPE_PRESSURE, this);
                        recyclerView.setAdapter(pressureAdapter);
                        break;
                    case R.id.recyclerview_humidity:
                        humidityAdapter = new SensorAdapter(getContext(), Constants.TYPE_HUMIDITY, this);
                        recyclerView.setAdapter(humidityAdapter);
                        break;
                    case R.id.recyclerview_iaq:
                        airqualityAdapter = new SensorAdapter(getContext(), Constants.TYPE_AIR_QUALITY, this);
                        recyclerView.setAdapter(airqualityAdapter);
                        break;
                    case R.id.recyclerview_luminosity:
                        luminosityAdapter = new SensorAdapter(getContext(), Constants.TYPE_LUMINOSITY, this);
                        recyclerView.setAdapter(luminosityAdapter);
                        break;
                    case R.id.recyclerview_acceleration:
                        accelerationAdapter = new SensorAdapter(getContext(), Constants.TYPE_ACCELERATION, this);
                        recyclerView.setAdapter(accelerationAdapter);
                        break;
                    case R.id.recyclerview_angular_velocity:
                        angularVelocityAdapter = new SensorAdapter(getContext(), Constants.TYPE_ANGULAR_VELOCITY, this);
                        recyclerView.setAdapter(angularVelocityAdapter);
                        break;
                    case R.id.recyclerview_magnetic_field:
                        magneticFieldAdapter = new SensorAdapter(getContext(), Constants.TYPE_MAGNETIC_FIELD, this);
                        recyclerView.setAdapter(magneticFieldAdapter);
                        break;
                }
            }
        );
    }
}
