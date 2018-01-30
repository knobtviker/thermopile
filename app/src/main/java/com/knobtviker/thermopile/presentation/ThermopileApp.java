package com.knobtviker.thermopile.presentation;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.knobtviker.android.things.contrib.driver.bme280.BME280SensorDriver;
import com.knobtviker.android.things.contrib.driver.bme680.Bme680;
import com.knobtviker.android.things.contrib.driver.bme680.Bme680SensorDriver;
import com.knobtviker.android.things.contrib.driver.tsl2561.TSL2561SensorDriver;
import com.knobtviker.android.things.device.RxScreenManager;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.Altitude;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.sources.local.implemenatation.Database;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.presenters.ApplicationPresenter;
import com.knobtviker.thermopile.presentation.utils.BoardDefaults;
import com.knobtviker.thermopile.presentation.utils.Router;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTimeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by bojan on 15/07/2017.
 */

// /data/data/com.knobtviker.thermopile
public class ThermopileApp extends Application implements SensorEventListener, ApplicationContract.View {
    private static final String TAG = ThermopileApp.class.getSimpleName();

    @NonNull
    private ApplicationContract.Presenter presenter;

//    private Temperature temperature;
//    private Pressure pressure;
//    private Humidity humidity;
//    private Altitude altitude;
//    private AirQuality airQuality;

    private List<Temperature> temperatures = Collections.synchronizedList(new ArrayList<>());
    private List<Pressure> pressures = Collections.synchronizedList(new ArrayList<>());
    private List<Humidity> humidities = Collections.synchronizedList(new ArrayList<>());
    private List<Altitude> altitudes = Collections.synchronizedList(new ArrayList<>());
    private List<AirQuality> airQualities = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void onCreate() {
        super.onCreate();

        initSensors();
        initDatabase();
        initStetho();
        initCalligraphy();
        initJodaTime();
        initPresenter();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                final Temperature temperature = new Temperature();
                temperature.timestamp(DateTimeUtils.currentTimeMillis());
                temperature.value(sensorEvent.values[0]);
                temperature.accuracy(sensorEvent.accuracy);
                temperature.vendor(sensorEvent.sensor.getVendor());
                temperature.name(sensorEvent.sensor.getName());

//                Log.i(TAG, "Temperature ---> " + sensorEvent.sensor.getVendor() + " --- " + sensorEvent.sensor.getName() + " --- " + sensorEvent.values[0]);

//                this.temperature = temperature;
                temperatures.add(temperature);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                final Humidity humidity = new Humidity();
                humidity.timestamp(DateTimeUtils.currentTimeMillis());
                humidity.value(sensorEvent.values[0]);
                humidity.accuracy(sensorEvent.accuracy);
                humidity.vendor(sensorEvent.sensor.getVendor());
                humidity.name(sensorEvent.sensor.getName());

//                Log.i(TAG, "Humidity ---> " + sensorEvent.sensor.getVendor() + " --- " + sensorEvent.sensor.getName() + " --- " + sensorEvent.values[0]);

//                this.humidity = humidity;
                humidities.add(humidity);
                break;
            case Sensor.TYPE_PRESSURE:
                final long now = DateTimeUtils.currentTimeMillis();

                final Pressure pressure = new Pressure();
                pressure.timestamp(now);
                pressure.value(sensorEvent.values[0]);
                pressure.accuracy(sensorEvent.accuracy);
                pressure.vendor(sensorEvent.sensor.getVendor());
                pressure.name(sensorEvent.sensor.getName());

//                Log.i(TAG, "Pressure ---> " + sensorEvent.sensor.getVendor() + " --- " + sensorEvent.sensor.getName() + " --- " + sensorEvent.values[0]);
                pressures.add(pressure);

                final Altitude altitude = new Altitude();
                altitude.timestamp(now);
                altitude.value(SensorManager.getAltitude(sensorEvent.values[0], SensorManager.PRESSURE_STANDARD_ATMOSPHERE));
                altitude.accuracy(sensorEvent.accuracy);
                altitude.vendor(sensorEvent.sensor.getVendor());
                altitude.name(sensorEvent.sensor.getName());

//                this.pressure = pressure;
//                this.altitude = altitude;
                altitudes.add(altitude);
                break;
            case Sensor.TYPE_DEVICE_PRIVATE_BASE:
                if (sensorEvent.sensor.getStringType().equals(Bme680.CHIP_SENSOR_TYPE_IAQ)) {
                    if (sensorEvent.sensor.getName().equals(Bme680.CHIP_NAME)) {
                        final AirQuality airQuality = new AirQuality();
                        airQuality.timestamp(DateTimeUtils.currentTimeMillis());
                        airQuality.value(sensorEvent.values[Bme680SensorDriver.INDOOR_AIR_QUALITY_INDEX]);
                        airQuality.accuracy(sensorEvent.accuracy);
                        airQuality.vendor(sensorEvent.sensor.getVendor());
                        airQuality.name(sensorEvent.sensor.getName());

//                        Log.i(TAG, "AirQuality ---> " + sensorEvent.sensor.getVendor() + " --- " + sensorEvent.sensor.getName() + " --- " + sensorEvent.values[Bme680SensorDriver.INDOOR_AIR_QUALITY_INDEX]);

//                        this.airQuality = airQuality;
                        airQualities.add(airQuality);
                    }
                }
                break;
            case Sensor.TYPE_LIGHT:
                //TODO: Fix this when Google fixes automatic brightness. This shows way bigger values than sensor maximum
//                Log.i(TAG, "Measured: " + sensorEvent.values[0] + " lx --- Fitted: " + TSL2561SensorDriver.getFittedLuminosity(sensorEvent.values[0])+ " lx --- Screen brightness: " + TSL2561SensorDriver.getScreenBrightness(sensorEvent.values[0]));
                break;

            //TODO: Add seismic accelearation approx. per:
            //https://en.wikipedia.org/wiki/Richter_magnitude_scale
            //https://en.wikipedia.org/wiki/Peak_ground_acceleration
            //https://en.wikipedia.org/wiki/Mercalli_intensity_scale#Modified_Mercalli_Intensity_scale
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                break;
            case Sensor.TYPE_PRESSURE:
                break;
            case Sensor.TYPE_LIGHT:
                break;
            case Sensor.TYPE_DEVICE_PRIVATE_BASE:
                if (sensor.getStringType().equals(Bme680.CHIP_SENSOR_TYPE_IAQ)) {
                    // ...
                }
                break;
        }
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    @Override
    public void onTick() {
//        if (this.temperature != null) {
//            presenter.saveTemperature(this.temperature);
//        }
//        if (this.pressure != null) {
//            presenter.savePressure(this.pressure);
//        }
//        if (this.humidity != null) {
//            presenter.saveHumidity(this.humidity);
//        }
//        if (this.altitude != null) {
//            presenter.saveAltitude(this.altitude);
//        }
//        if (this.airQuality != null) {
//            presenter.saveAirQuality(this.airQuality);
//        }
        save();
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

    private void initDatabase() {
        Database.init(this);
    }

    private void initStetho() {
        Stetho.initialize(
            Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(
                    RealmInspectorModulesProvider.builder(this)
                        .withLimit(1000)
                        .withDescendingOrder()
                        .build()
                )
                .build());
    }

    private void initPresenter() {
        presenter = new ApplicationPresenter(this);
        presenter.subscribe();
        presenter.tick();
        presenter.initScreen(160, RxScreenManager.ROTATION_180, 3600000L);
        presenter.createScreensaver();
        presenter.brightness(255);
    }

    private void initSensors() {
        registerSensorCallback();

        try {
            initBME280();
            initBME680();
            initTSL2561();
        } catch (IOException e) {
            showError(e);
        }
    }

    private void initCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/WorkSans-Regular.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build());
    }

    private void initJodaTime() {
        JodaTimeAndroid.init(this);
    }

    private void registerSensorCallback() {
        final SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorManager.registerDynamicSensorCallback(new SensorManager.DynamicSensorCallback() {
                @Override
                public void onDynamicSensorConnected(Sensor sensor) {
                    registerListener(sensorManager, sensor);
                }
            });
        }
    }

    private void registerListener(@NonNull final SensorManager sensorManager, @NonNull final Sensor sensor) {
        switch (sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
            case Sensor.TYPE_RELATIVE_HUMIDITY:
            case Sensor.TYPE_PRESSURE:
            case Sensor.TYPE_LIGHT:
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            case Sensor.TYPE_DEVICE_PRIVATE_BASE:
                if (sensor.getStringType().equals(Bme680.CHIP_SENSOR_TYPE_IAQ)) {
                    sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
                break;
        }
    }

    //CHIP_ID_BME280 = 0x60 | DEFAULT_I2C_ADDRESS = 0x77
    private void initBME280() throws IOException {
        final BME280SensorDriver bme280SensorDriver = new BME280SensorDriver(BoardDefaults.getI2CPort());
        bme280SensorDriver.registerTemperatureSensor();
        bme280SensorDriver.registerPressureSensor();
        bme280SensorDriver.registerHumiditySensor();
    }

    //CHIP_ID_BME680 = 0x61 | DEFAULT_I2C_ADDRESS = 0x76 (or 0x77)
    private void initBME680() throws IOException {
        final Bme680SensorDriver bme680SensorDriver = new Bme680SensorDriver(BoardDefaults.getI2CPort());
        bme680SensorDriver.registerTemperatureSensor();
        bme680SensorDriver.registerPressureSensor();
        bme680SensorDriver.registerHumiditySensor();
        bme680SensorDriver.registerGasSensor();
    }

    //CHIP_ID_TSL2561 = 0x?? | DEFAULT_I2C_ADDRESS = 0x39 (or 0x29 or 0x49)
    private void initTSL2561() throws IOException {
        final TSL2561SensorDriver tsl2561SensorDriver = new TSL2561SensorDriver(BoardDefaults.getI2CPort());
        tsl2561SensorDriver.registerLuminositySensor();
    }

    private void brightness(final int value) {
        presenter.brightness(value);
    }

    private boolean isThingsDevice(@NonNull final Context context) {
        return context
            .getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_EMBEDDED);
    }

    private synchronized void save() {
        if (!temperatures.isEmpty()) {
            presenter.saveTemperature(new ArrayList<>(temperatures));
            temperatures.clear();
        }
        if (!pressures.isEmpty()) {
            presenter.savePressure(new ArrayList<>(pressures));
            pressures.clear();
        }
        if (!humidities.isEmpty()) {
            presenter.saveHumidity(new ArrayList<>(humidities));
            humidities.clear();
        }
        if (!altitudes.isEmpty()) {
            presenter.saveAltitude(new ArrayList<>(altitudes));
            altitudes.clear();
        }
        if (!airQualities.isEmpty()) {
            presenter.saveAirQuality(new ArrayList<>(airQualities));
            airQualities.clear();
        }
    }
}
