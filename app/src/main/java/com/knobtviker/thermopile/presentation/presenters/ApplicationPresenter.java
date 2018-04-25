package com.knobtviker.thermopile.presentation.presenters;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.common.collect.ImmutableList;
import com.knobtviker.android.things.contrib.community.boards.I2CDevice;
import com.knobtviker.android.things.contrib.community.driver.bme680.Bme680;
import com.knobtviker.android.things.contrib.community.driver.bme680.Bme680SensorDriver;
import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.di.components.data.DaggerAtmosphereDataComponent;
import com.knobtviker.thermopile.di.components.data.DaggerPeripheralsDataComponent;
import com.knobtviker.thermopile.di.components.data.DaggerSettingsDataComponent;
import com.knobtviker.thermopile.di.components.domain.DaggerSchedulerProviderComponent;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.PeripheralsRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;
import com.knobtviker.thermopile.presentation.utils.factories.IntentFactory;
import com.knobtviker.thermopile.presentation.utils.predicates.PeripheralDevicePredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 08/08/2017.
 */

public class ApplicationPresenter extends AbstractPresenter implements ApplicationContract.Presenter {

    private final ApplicationContract.View view;

    private final AtmosphereRepository atmosphereRepository;

    private final SettingsRepository settingsRepository;

    private final PeripheralsRepository peripheralsRepository;

    private final Scheduler scheduler;

    private List<PeripheralDevice> peripherals;

    private RealmResults<Settings> resultsSettings;

    private RealmResults<PeripheralDevice> resultsPeripherals;

    @Nullable
    private Disposable screensaverDisposable;

    public ApplicationPresenter(@NonNull final ApplicationContract.View view) {
        super(view);

        this.view = view;
        this.atmosphereRepository = DaggerAtmosphereDataComponent.create().repository();
        this.settingsRepository = DaggerSettingsDataComponent.create().repository();
        this.peripheralsRepository = DaggerPeripheralsDataComponent.create().repository();
        this.scheduler = DaggerSchedulerProviderComponent.create().scheduler().screensaver;
    }

    @Override
    public void addListeners() {

    }

    @Override
    public void removeListeners() {
        if (resultsSettings != null && resultsSettings.isValid()) {
            resultsSettings.removeAllChangeListeners();
        }
        if (resultsPeripherals != null && resultsPeripherals.isValid()) {
            resultsPeripherals.removeAllChangeListeners();
        }
    }

    @Override
    public void observeSensors(@NonNull Context context) {
        peripherals = new ArrayList<>(0);

        final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        final String packageName = context.getPackageName();

        compositeDisposable.addAll(
            peripheralsRepository
                .observeSensors(context)
                .subscribe(
                    sensorEvent -> {
                        switch (sensorEvent.sensor.getType()) {
                            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                                if (peripherals
                                    .stream()
                                    .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
                                    ) {
                                    localBroadcastManager.sendBroadcast(IntentFactory.temperature(packageName, sensorEvent.values[0]));
                                }
                                break;
                            case Sensor.TYPE_RELATIVE_HUMIDITY:
                                if (peripherals
                                    .stream()
                                    .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
                                    ) {
                                    localBroadcastManager.sendBroadcast(IntentFactory.humidity(packageName, sensorEvent.values[0]));
                                }
                                break;
                            case Sensor.TYPE_PRESSURE:
                                if (peripherals
                                    .stream()
                                    .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
                                    ) {
                                    localBroadcastManager.sendBroadcast(IntentFactory.pressure(packageName, sensorEvent.values[0]));

                                    final float altitudeValue = SensorManager.getAltitude(sensorEvent.values[0], SensorManager.PRESSURE_STANDARD_ATMOSPHERE);
                                    localBroadcastManager.sendBroadcast(IntentFactory.altitude(packageName, altitudeValue));
                                }
                                break;
                            case Sensor.TYPE_LIGHT:
                                if (peripherals
                                    .stream()
                                    .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
                                    ) {
                                    localBroadcastManager.sendBroadcast(IntentFactory.luminosity(packageName, sensorEvent.values[0]));
                                    //  TODO: Google dropped Automatic Brightness Mode in DP7. Do your own math with manual mode. Less light == lower screen brightness.
                                    // Log.i(TAG, "Measured: " + sensorEvent.values[0] + " lx --- Fitted: " + TSL2561SensorDriver.getFittedLuminosity(sensorEvent.values[0]) + " lx --- Screen brightness: " + TSL2561SensorDriver.getScreenBrightness(sensorEvent.values[0]));
                                }
                                break;
                            case Sensor.TYPE_ACCELEROMETER: //[m/s^2]
                                if (peripherals
                                    .stream()
                                    .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
                                    ) {
                                    localBroadcastManager.sendBroadcast(IntentFactory.acceleration(packageName, new float[]{sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]}));
                                }
                                break;
                            case Sensor.TYPE_GYROSCOPE: //[°/s]
                                if (peripherals
                                    .stream()
                                    .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
                                    ) {
                                    localBroadcastManager.sendBroadcast(IntentFactory.angularVelocity(packageName, new float[]{sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]}));
                                }
                                break;
                            case Sensor.TYPE_MAGNETIC_FIELD:
                                if (peripherals
                                    .stream()
                                    .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
                                    ) {
                                    localBroadcastManager.sendBroadcast(IntentFactory.magneticField(packageName, new float[]{sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]}));
                                }
                                break;
                            case Sensor.TYPE_DEVICE_PRIVATE_BASE:
                                if (sensorEvent.sensor.getStringType().equals(Bme680.CHIP_SENSOR_TYPE_IAQ)) {
                                    if (peripherals
                                        .stream()
                                        .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
                                        ) {
                                        localBroadcastManager.sendBroadcast(IntentFactory.airQuality(packageName, sensorEvent.values[Bme680SensorDriver.INDOOR_AIR_QUALITY_INDEX]));
                                    }
                                    break;
                                }
                                break;
                        }
                    }
                ),
            Observable
                .defer(() -> peripheralsRepository.observeTemperatureBuffered(context))
                .subscribe(
                    atmosphereRepository::saveTemperature,
                    this::error
                ),
            Observable
                .defer(() -> peripheralsRepository.observePressureBuffered(context))
                .subscribe(
                    atmosphereRepository::savePressure,
                    this::error
                ),
            Observable
                .defer(() -> peripheralsRepository.observeAltitudeBuffered(context))
                .subscribe(
                    atmosphereRepository::saveAltitude,
                    this::error
                ),
            Observable
                .defer(() -> peripheralsRepository.observeHumidityBuffered(context))
                .subscribe(
                    atmosphereRepository::saveHumidity,
                    this::error
                ),
            Observable
                .defer(() -> peripheralsRepository.observeAirQualityBuffered(context))
                .subscribe(
                    atmosphereRepository::saveAirQuality,
                    this::error
                ),
            Observable
                .defer(() -> peripheralsRepository.observeLuminosityBuffered(context))
                .subscribe(
                    atmosphereRepository::saveLuminosity,
                    this::error
                ),
            Observable
                .defer(() -> peripheralsRepository.observeAccelerationBuffered(context))
                .subscribe(
                    atmosphereRepository::saveAccelerations,
                    this::error
                ),
            Observable
                .defer(() -> peripheralsRepository.observeAngularVelocityBuffered(context))
                .subscribe(
                    atmosphereRepository::saveAngularVelocities,
                    this::error
                ),
            Observable
                .defer(() -> peripheralsRepository.observeMagneticFieldBuffered(context))
                .subscribe(
                    atmosphereRepository::saveMagneticFields,
                    this::error
                )
        );
    }

    @Override
    public void createScreensaver() {
//        //TODO: Timer delay for screensaver should be loaded from Settings.
        screensaverDisposable = Completable
            .timer(60, TimeUnit.SECONDS, scheduler)
            .observeOn(scheduler)
            .subscribe(
                view::showScreensaver,
                this::error
            );
    }

    @Override
    public void destroyScreensaver() {
        if (screensaverDisposable != null && !screensaverDisposable.isDisposed()) {
            screensaverDisposable.dispose();
            screensaverDisposable = null;
        }
    }

    @Override
    public void settings(@NonNull final Realm realm) {
        resultsSettings = settingsRepository.load(realm);

        if (!resultsSettings.isEmpty()) {
            final Settings result = resultsSettings.first();
            if (result != null) {
                view.onSettings(result);
            }
        }
    }

    @Override
    public ImmutableList<I2CDevice> i2cDevices() {
        return ImmutableList.copyOf(peripheralsRepository.probe());
    }

    @Override
    public void peripherals(@NonNull final Realm realm) {
        resultsPeripherals = peripheralsRepository.load(realm);

        if (resultsPeripherals != null && !resultsPeripherals.isEmpty()) {
            peripherals = realm.copyFromRealm(resultsPeripherals);
            resultsPeripherals.addChangeListener(peripheralDevices -> peripherals = realm.copyFromRealm(resultsPeripherals));
        }
    }

    @Override
    public RealmResults<PeripheralDevice> defaultSensors(@NonNull Realm realm) {
        return peripheralsRepository.load(realm);
    }

    @Override
    public void saveDefaultSensors(@NonNull List<PeripheralDevice> foundSensors) {
        peripheralsRepository.saveConnected(foundSensors, true);
    }
}
