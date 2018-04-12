package com.knobtviker.thermopile.presentation;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDelegate;

import com.google.android.things.device.TimeManager;
import com.google.common.collect.ImmutableList;
import com.knobtviker.android.things.contrib.community.boards.BoardDefaults;
import com.knobtviker.android.things.contrib.community.boards.I2CDevice;
import com.knobtviker.android.things.contrib.community.driver.bme280.BME280SensorDriver;
import com.knobtviker.android.things.contrib.community.driver.bme680.Bme680SensorDriver;
import com.knobtviker.android.things.contrib.community.driver.ds3231.Ds3231;
import com.knobtviker.android.things.contrib.community.driver.ds3231.Ds3231SensorDriver;
import com.knobtviker.android.things.contrib.community.driver.lsm9ds1.Lsm9ds1SensorDriver;
import com.knobtviker.android.things.contrib.community.driver.tsl2561.TSL2561SensorDriver;
import com.knobtviker.android.things.contrib.community.support.rxscreenmanager.RxScreenManager;
import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.sources.local.implemenatation.Database;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.presenters.ApplicationPresenter;
import com.knobtviker.thermopile.presentation.utils.Router;
import com.knobtviker.thermopile.presentation.utils.factories.IntentFactory;
import com.knobtviker.thermopile.presentation.utils.predicates.PeripheralDevicePredicate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import timber.log.Timber;

/**
 * Created by bojan on 15/07/2017.
 */

// /data/data/com.knobtviker.thermopile
public class ThermopileApplication extends AbstractApplication<ApplicationContract.Presenter> implements ApplicationContract.View {
    private static final String TAG = ThermopileApplication.class.getSimpleName();

    private LocalBroadcastManager localBroadcastManager;

    private List<PeripheralDevice> peripherals = new ArrayList<>(0);

    @Override
    public void onCreate() {
        super.onCreate();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        initPresenter();
    }

    @Override
    protected void onTemperatureChanged(@NonNull SensorEvent sensorEvent) {
        if (peripherals
            .stream()
            .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
            ) {
            broadcastTemperature(sensorEvent.values[0]);
        }
    }

    @Override
    protected void onPressureChanged(@NonNull SensorEvent sensorEvent) {
        if (peripherals
            .stream()
            .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
            ) {
            broadcastPressure(sensorEvent.values[0]);

            final float altitudeValue = SensorManager.getAltitude(sensorEvent.values[0], SensorManager.PRESSURE_STANDARD_ATMOSPHERE);
            broadcastAltitude(altitudeValue);
        }
    }

    @Override
    protected void onHumidityChanged(@NonNull SensorEvent sensorEvent) {
        if (peripherals
            .stream()
            .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
            ) {
            broadcastHumidity(sensorEvent.values[0]);
        }
    }

    @Override
    protected void onAirQualityChanged(@NonNull SensorEvent sensorEvent) {
        if (peripherals
            .stream()
            .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
            ) {
            broadcastAirQuality(sensorEvent.values[Bme680SensorDriver.INDOOR_AIR_QUALITY_INDEX]);
        }
    }

    @Override
    protected void onLuminosityChanged(@NonNull SensorEvent sensorEvent) {
        if (peripherals
            .stream()
            .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
            ) {
            broadcastLuminosity(sensorEvent.values[0]);
            //TODO: Google dropped Automatic Brightness Mode in DP7. Do your own math with manual mode. Less light == lower screen brightness.
//            Log.i(TAG, "Measured: " + sensorEvent.values[0] + " lx --- Fitted: " + TSL2561SensorDriver.getFittedLuminosity(sensorEvent.values[0]) + " lx --- Screen brightness: " + TSL2561SensorDriver.getScreenBrightness(sensorEvent.values[0]));
        }
    }

    @Override
    protected void onAccelerationChanged(@NonNull SensorEvent sensorEvent) {
        if (peripherals
            .stream()
            .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
            ) {
            broadcastAcceleration(new float[]{sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]});
        }
    }

    @Override
    protected void onAngularVelocityChanged(@NonNull SensorEvent sensorEvent) {
        if (peripherals
            .stream()
            .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
            ) {
            broadcastAngularVelocity(new float[]{sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]});
        }
    }

    @Override
    protected void onMagneticFieldChanged(@NonNull SensorEvent sensorEvent) {
        if (peripherals
            .stream()
            .anyMatch(PeripheralDevicePredicate.allowed(sensorEvent.sensor))
            ) {
            broadcastMagneticField(new float[]{sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]});
        }
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);
    }

    @Override
    public void onSettings(@NonNull Settings settings) {
        AppCompatDelegate.setDefaultNightMode(settings.theme());
    }

    @Override
    public void onPeripherals(@NonNull RealmResults<PeripheralDevice> peripheralDevices) {
        this.peripherals = peripheralDevices.subList(0, peripheralDevices.size());
    }

    @Override
    public void showScreensaver() {
        Router.showScreensaver(this);
        brightness(24);
    }

    public void createScreensaver() {
        brightness(255);
        presenter.createScreensaver();
    }

    public void destroyScreensaver() {
        presenter.destroyScreensaver();
    }

    private void initPresenter() {
        presenter = new ApplicationPresenter(this);

        presenter.observeTemperatureChanged(this);
        presenter.observePressureChanged(this);
        presenter.observeAltitudeChanged(this);
        presenter.observeHumidityChanged(this);
        presenter.observeAirQualityChanged(this);
        presenter.observeLuminosityChanged(this);
        presenter.observeAccelerationChanged(this);
        presenter.observeAngularVelocityChanged(this);
        presenter.observeMagneticFieldChanged(this);

        presenter.initScreen(160, RxScreenManager.ROTATION_180);
        presenter.brightness(255);
        presenter.createScreensaver();

        presenter.settings(Database.getDefaultInstance());

        initSensors();
    }

    private void initSensors() {
        final ImmutableList<I2CDevice> i2CDevices = presenter.i2cDevices();

        if (i2CDevices.isEmpty()) {
            //TODO: Show in Main that there are no sensors connected
            return;
        }

        registerSensorCallback();

        final List<PeripheralDevice> foundSensors = new ArrayList<>(0);

        presenter
            .defaultSensors(Database.getDefaultInstance())
            .forEach(
                defaultSensor -> {
                    boolean found = i2CDevices
                        .stream()
                        .map(I2CDevice::address)
                        .anyMatch(integer -> integer == defaultSensor.address());

                    if (found) {
                        foundSensors.add(defaultSensor);

                        try {
                            //TODO: Move these hardcoded addresses to some Integrity with default constants class
                            switch (defaultSensor.address()) {
                                case 0x77:
                                    initBME280();
                                    break;
                                case 0x76:
                                    initBME680();
                                    break;
                                case 0x68:
                                    initDS3231();
                                    break;
                                case 0x39:
                                    initTSL2561();
                                    break;
                                case 0x6B:
                                    initLSM9DS1();
                                    break;
                            }
                        } catch (IOException e) {
                            showError(e);
                        }
                    }
                }
            );

        if (!foundSensors.isEmpty()) {
            presenter.saveDefaultSensors(foundSensors);
        }

        presenter.peripherals(Database.getDefaultInstance());
    }

    //CHIP_ID_BME280 = 0x60 | DEFAULT_I2C_ADDRESS = 0x77
    private void initBME280() throws IOException {
        final BME280SensorDriver bme280SensorDriver = new BME280SensorDriver(BoardDefaults.defaultI2CBus());
        bme280SensorDriver.registerTemperatureSensor();
        bme280SensorDriver.registerPressureSensor();
        bme280SensorDriver.registerHumiditySensor();
    }

    //CHIP_ID_BME680 = 0x61 | DEFAULT_I2C_ADDRESS = 0x76 (or 0x77)
    private void initBME680() throws IOException {
        final Bme680SensorDriver bme680SensorDriver = new Bme680SensorDriver(BoardDefaults.defaultI2CBus());
        bme680SensorDriver.registerTemperatureSensor();
        bme680SensorDriver.registerPressureSensor();
        bme680SensorDriver.registerHumiditySensor();
        bme680SensorDriver.registerGasSensor();
    }

    //CHIP_ID_DS3231 = 0x?? | DEFAULT_I2C_ADDRESS = 0x68
    private void initDS3231() throws IOException {
        final Ds3231SensorDriver ds3231SensorDriver = new Ds3231SensorDriver(BoardDefaults.defaultI2CBus());
        ds3231SensorDriver.registerTemperatureSensor();

        final Ds3231 ds3231 = ds3231SensorDriver.device();
        if (ds3231 != null) {
            final long ds3231Timestamp = ds3231.getTimeInMillis();
            final long systemTimeStamp = System.currentTimeMillis();
            // If the DS3231 has a sane timestamp, set the system clock using the DS3231.
            // Otherwise, set the DS3231 using the system time if the system time appears sane
            if (ds3231Timestamp >= Environment.getRootDirectory().lastModified()) {
//                Log.i(TAG, "Setting system clock using DS3231");
                final TimeManager timeManager = TimeManager.getInstance();
                timeManager.setTime(ds3231Timestamp);

                // Re-enable NTP updates.
                // The call to setTime() disables them automatically, but that's what we use to update our DS3231.
                timeManager.setAutoTimeEnabled(true);
            } else if (systemTimeStamp >= Environment.getRootDirectory().lastModified()) {
//                Log.i(TAG, "Setting DS3231 time using system clock");
                ds3231.setTime(systemTimeStamp);
            }
        }
    }

    //CHIP_ID_TSL2561 = 0x?? | DEFAULT_I2C_ADDRESS = 0x39 (or 0x29 or 0x49)
    private void initTSL2561() throws IOException {
        final TSL2561SensorDriver tsl2561SensorDriver = new TSL2561SensorDriver(BoardDefaults.defaultI2CBus());
        tsl2561SensorDriver.registerLuminositySensor();
    }

    //CHIP_ID_LSM9DS1 = 0x?? | DEFAULT_I2C_ADDRESS_ACCEL_GYRO = 0x6B | DEFAULT_I2C_ADDRESS_MAG = 0x1E
    private void initLSM9DS1() throws IOException {
        final Lsm9ds1SensorDriver lsm9ds1SensorDriver = new Lsm9ds1SensorDriver(BoardDefaults.defaultI2CBus());
        lsm9ds1SensorDriver.registerTemperatureSensor();
        lsm9ds1SensorDriver.registerAccelerometerSensor();
        lsm9ds1SensorDriver.registerGyroscopeSensor();
        lsm9ds1SensorDriver.registerMagneticFieldSensor();
    }

    private void brightness(final int value) {
        presenter.brightness(value);
    }

    private void broadcastTemperature(final float value) {
        localBroadcastManager.sendBroadcast(IntentFactory.temperature(this, value));
    }

    private void broadcastPressure(final float value) {
        localBroadcastManager.sendBroadcast(IntentFactory.pressure(this, value));
    }

    private void broadcastAltitude(final float value) {
        localBroadcastManager.sendBroadcast(IntentFactory.altitude(this, value));
    }

    private void broadcastHumidity(final float value) {
        localBroadcastManager.sendBroadcast(IntentFactory.humidity(this, value));
    }

    private void broadcastAirQuality(final float value) {
        localBroadcastManager.sendBroadcast(IntentFactory.airQuality(this, value));
    }

    private void broadcastLuminosity(final float value) {
        localBroadcastManager.sendBroadcast(IntentFactory.luminosity(this, value));
    }

    private void broadcastAcceleration(final float[] values) {
        localBroadcastManager.sendBroadcast(IntentFactory.acceleration(this, values));
    }

    private void broadcastAngularVelocity(final float[] values) {
        localBroadcastManager.sendBroadcast(IntentFactory.angularVelocity(this, values));
    }

    private void broadcastMagneticField(final float[] values) {
        localBroadcastManager.sendBroadcast(IntentFactory.magneticField(this, values));
    }

    private boolean isThingsDevice(@NonNull final Context context) {
        return context
            .getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_EMBEDDED);
    }
}
