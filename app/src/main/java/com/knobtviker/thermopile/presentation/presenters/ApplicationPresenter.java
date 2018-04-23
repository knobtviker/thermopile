package com.knobtviker.thermopile.presentation.presenters;

import android.content.Context;
import android.hardware.Sensor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;

import com.google.common.collect.ImmutableList;
import com.knobtviker.android.things.contrib.community.boards.I2CDevice;
import com.knobtviker.android.things.contrib.community.driver.bme680.Bme680;
import com.knobtviker.android.things.contrib.community.support.rxscreenmanager.RxScreenManager;
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

    private final RxScreenManager rxScreenManager;

    private final Scheduler scheduler;

    private RealmResults<Settings> resultsSettings;

    private RealmResults<PeripheralDevice> resultsPeripherals;

    @Nullable
    private Disposable screensaverDisposable;

    public ApplicationPresenter(@NonNull final Context context, @NonNull final ApplicationContract.View view) {
        super(view);

        this.view = view;
        this.atmosphereRepository = DaggerAtmosphereDataComponent.create().repository();
        this.settingsRepository = DaggerSettingsDataComponent.create().repository();
        this.peripheralsRepository = DaggerPeripheralsDataComponent.create().repository();
        this.scheduler = DaggerSchedulerProviderComponent.create().scheduler().screensaver;
        this.rxScreenManager = RxScreenManager.create(Display.DEFAULT_DISPLAY);
    }

    @Override
    public void addListeners() {
        if (resultsSettings != null && resultsSettings.isValid()) {
            resultsSettings.addChangeListener(settings -> {
                if (!settings.isEmpty()) {
                    final Settings result = settings.first();
                    if (result != null) {
                        view.onSettings(result);
                    }
                }
            });
        }
        if (resultsPeripherals != null && resultsPeripherals.isValid()) {
            resultsPeripherals.addChangeListener(view::onPeripherals);
        }
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
    public void observeTemperatureChanged(@NonNull Context context) {
        compositeDisposable.add(
            Observable
                .defer(() -> peripheralsRepository.observeTemperatureBuffered(context))
                .subscribe(
                    atmosphereRepository::saveTemperature,
                    this::error
                )
        );
    }

    @Override
    public void observePressureChanged(@NonNull Context context) {
        compositeDisposable.add(
            Observable
                .defer(() -> peripheralsRepository.observePressureBuffered(context))
                .subscribe(
                    atmosphereRepository::savePressure,
                    this::error
                )
        );
    }

    @Override
    public void observeAltitudeChanged(@NonNull Context context) {
        compositeDisposable.add(
            Observable
                .defer(() -> peripheralsRepository.observeAltitudeBuffered(context))
                .subscribe(
                    atmosphereRepository::saveAltitude,
                    this::error
                )
        );
    }

    @Override
    public void observeHumidityChanged(@NonNull Context context) {
        compositeDisposable.add(
            Observable
                .defer(() -> peripheralsRepository.observeHumidityBuffered(context))
                .subscribe(
                    atmosphereRepository::saveHumidity,
                    this::error
                )
        );
    }

    @Override
    public void observeAirQualityChanged(@NonNull Context context) {
        compositeDisposable.add(
            Observable
                .defer(() -> peripheralsRepository.observeAirQualityBuffered(context))
                .subscribe(
                    atmosphereRepository::saveAirQuality,
                    this::error
                )
        );
    }

    @Override
    public void observeLuminosityChanged(@NonNull Context context) {
        compositeDisposable.add(
            Observable
                .defer(() -> peripheralsRepository.observeLuminosityBuffered(context))
                .subscribe(
                    atmosphereRepository::saveLuminosity,
                    this::error
                )
        );
    }

    @Override
    public void observeAccelerationChanged(@NonNull Context context) {
        compositeDisposable.add(
            Observable
                .defer(() -> peripheralsRepository.observeAccelerationBuffered(context))
                .subscribe(
                    atmosphereRepository::saveAccelerations,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void observeAngularVelocityChanged(@NonNull Context context) {
        compositeDisposable.add(
            Observable
                .defer(() -> peripheralsRepository.observeAngularVelocityBuffered(context))
                .subscribe(
                    atmosphereRepository::saveAngularVelocities,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void observeMagneticFieldChanged(@NonNull Context context) {
        compositeDisposable.add(
            Observable
                .defer(() -> peripheralsRepository.observeMagneticFieldBuffered(context))
                .subscribe(
                    atmosphereRepository::saveMagneticFields,
                    this::error,
                    this::completed
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
    public void initScreen(int density, int rotation) {
        Completable.concatArray(
            rxScreenManager.lockRotation(rotation),
            rxScreenManager.displayDensity(density)
        )
            .subscribe(
                this::completed,
                this::error
            );
    }

    @Override
    public void brightness(int brightness) {
        rxScreenManager
            .brightness(brightness)
            .subscribe(
                this::completed,
                this::error
            );
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
            view.onPeripherals(resultsPeripherals);
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

    @Override
    public void observeSensors(@NonNull Context context) {
        compositeDisposable.add(
            peripheralsRepository
                .observeSensors(context)
                .subscribe(
                    sensorEvent -> {
                        switch (sensorEvent.sensor.getType()) {
                            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                                view.onTemperatureChanged(sensorEvent);
                                break;
                            case Sensor.TYPE_RELATIVE_HUMIDITY:
                                view.onHumidityChanged(sensorEvent);
                                break;
                            case Sensor.TYPE_PRESSURE:
                                view.onPressureChanged(sensorEvent);
                                break;
                            case Sensor.TYPE_LIGHT:
                                view.onLuminosityChanged(sensorEvent);
                                break;
                            case Sensor.TYPE_ACCELEROMETER: //[m/s^2]
                                view.onAccelerationChanged(sensorEvent);
                                break;
                            case Sensor.TYPE_GYROSCOPE: //[°/s]
                                view.onAngularVelocityChanged(sensorEvent);
                                break;
                            case Sensor.TYPE_MAGNETIC_FIELD:
                                view.onMagneticFieldChanged(sensorEvent);
                                break;
                            case Sensor.TYPE_DEVICE_PRIVATE_BASE:
                                if (sensorEvent.sensor.getStringType().equals(Bme680.CHIP_SENSOR_TYPE_IAQ)) {
                                    view.onAirQualityChanged(sensorEvent);
                                    break;
                                }
                                break;
                        }
                    }
                )
        );
    }
}
