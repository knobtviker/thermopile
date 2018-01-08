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
import com.google.android.things.contrib.driver.bmx280.Bmx280SensorDriver;
import com.google.common.collect.ImmutableList;
import com.knobtviker.android.things.contrib.driver.tsl2561.TSL2561SensorDriver;
import com.knobtviker.android.things.device.RxScreenManager;
import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.Atmosphere;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.sources.raw.bme680.Bme680;
import com.knobtviker.thermopile.data.sources.raw.bme680.Bme680SensorDriver;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.presenters.ApplicationPresenter;
import com.knobtviker.thermopile.presentation.utils.BoardDefaults;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.utils.Router;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTimeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by bojan on 15/07/2017.
 */

// /data/data/com.knobtviker.thermopile
public class ThermopileApp extends Application implements SensorEventListener, ApplicationContract.View {
    private static final String TAG = ThermopileApp.class.getSimpleName();

    @NonNull
    private ApplicationContract.Presenter presenter;

    private Atmosphere atmosphere = new Atmosphere();

    @Override
    public void onCreate() {
        super.onCreate();

        initSensors();
        initRealm();
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

                this.atmosphere.temperature(temperature);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                final Humidity humidity = new Humidity();
                humidity.timestamp(DateTimeUtils.currentTimeMillis());
                humidity.value(sensorEvent.values[0]);
                humidity.accuracy(sensorEvent.accuracy);
                humidity.vendor(sensorEvent.sensor.getVendor());
                humidity.name(sensorEvent.sensor.getName());

                this.atmosphere.humidity(humidity);
                break;
            case Sensor.TYPE_PRESSURE:
                final Pressure pressure = new Pressure();
                pressure.timestamp(DateTimeUtils.currentTimeMillis());
                pressure.value(sensorEvent.values[0]);
                pressure.accuracy(sensorEvent.accuracy);
                pressure.vendor(sensorEvent.sensor.getVendor());
                pressure.name(sensorEvent.sensor.getName());

                this.atmosphere.pressure(pressure);
                break;
            case Sensor.TYPE_DEVICE_PRIVATE_BASE:
                if (sensorEvent.sensor.getStringType().equals(Bme680.CHIP_SENSOR_TYPE_IAQ)) {
                    Log.i(TAG, Bme680.CHIP_SENSOR_TYPE_IAQ + " --- Percentage: " + sensorEvent.values[0]+" --- IAQ index: " + Math.round(sensorEvent.values[0] * 500));

                    final AirQuality airQuality = new AirQuality();
                    airQuality.timestamp(DateTimeUtils.currentTimeMillis());
                    airQuality.value(sensorEvent.values[Bme680SensorDriver.INDOOR_AIR_QUALITY_INDEX]);
                    airQuality.accuracy(sensorEvent.accuracy);
                    airQuality.vendor(sensorEvent.sensor.getVendor());
                    airQuality.name(sensorEvent.sensor.getName());

                    this.atmosphere.airQuality(airQuality);

                    //IAQ classification and color-coding
                    /*
                        0 - 50 - good - #00e400
                        51 - 100 - average - #ffff00
                        101 - 200 - little bad - #ff7e00
                        201 - 300 - bad - #ff0000
                        301 - 400 - worse - #99004c
                        401 - 500 - very bad - #000000
                     */
                }
                break;
            case Sensor.TYPE_LIGHT:
//                Log.i(TAG, sensorEvent.values[0]+" lux");adb
                break;
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
        if (this.atmosphere.temperature() != null && this.atmosphere.humidity() != null && this.atmosphere.pressure() != null && this.atmosphere.airQuality() != null) {
            presenter.saveAtmosphere(atmosphere);
        }
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

    private void initRealm() {
        Realm.init(this);
        //TODO: Enable encryption
        final RealmConfiguration config = new RealmConfiguration.Builder()
            .name(BuildConfig.DATABASE_NAME)
            .schemaVersion(BuildConfig.DATABASE_VERSION)
//            .deleteRealmIfMigrationNeeded()
            .initialData(realm -> {
                realm.insert(defaultSettings());
                realm.insert(mockThresholds());
            })
            .build();

        Realm.setDefaultConfiguration(config);
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
        presenter.initScreen(160, RxScreenManager.ROTATION_180, 1800000L);
        presenter.createScreensaver();
        presenter.brightness(255);
    }

    private void initSensors() {
        registerSensorCallback();

//        initBME280();
        initBME680();
        initTSL2561();
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
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
                break;
            case Sensor.TYPE_DEVICE_PRIVATE_BASE:
                if (sensor.getStringType().equals(Bme680.CHIP_SENSOR_TYPE_IAQ)) {
                    sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
                }
                break;
        }
    }

    //CHIP_ID_BME280 = 0x60 | DEFAULT_I2C_ADDRESS = 0x77
    private void initBME280() {
        try {
            final Bmx280SensorDriver bmx280SensorDriver = new Bmx280SensorDriver(BoardDefaults.getI2CPort());
            bmx280SensorDriver.registerTemperatureSensor();
            bmx280SensorDriver.registerHumiditySensor();
            bmx280SensorDriver.registerPressureSensor();
        } catch (IOException e) {
            showError(e);
        }
    }

    //CHIP_ID_BME680 = 0x61 | DEFAULT_I2C_ADDRESS = 0x76 (or 0x77)
    private void initBME680() {
        try {
            final Bme680SensorDriver bme680SensorDriver = new Bme680SensorDriver(BoardDefaults.getI2CPort());
            bme680SensorDriver.registerTemperatureSensor();
            bme680SensorDriver.registerHumiditySensor();
            bme680SensorDriver.registerPressureSensor();
            bme680SensorDriver.registerGasSensor();
        } catch (IOException e) {
            showError(e);
        }
    }

    //CHIP_ID_TSL2561 = 0x?? | DEFAULT_I2C_ADDRESS = 0x39 (or 0x29 or 0x49)
    private void initTSL2561() {
        try {
            final TSL2561SensorDriver tsl2561SensorDriver = new TSL2561SensorDriver(BoardDefaults.getI2CPort());
            tsl2561SensorDriver.registerLuminositySensor();
        } catch (IOException e) {
            showError(e);
        }
    }

    private void brightness(final int value) {
        presenter.brightness(value);
    }

    private boolean isThingsDevice(@NonNull final Context context) {
        return context
            .getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_EMBEDDED);
    }

    private Settings defaultSettings() {
        final Settings settings = new Settings();

        settings.id(0L);
        settings.timezone(Constants.DEFAULT_TIMEZONE);
        settings.formatClock(Constants.CLOCK_MODE_24H);
        settings.unitTemperature(Constants.UNIT_TEMPERATURE_CELSIUS);
        settings.unitPressure(Constants.UNIT_PRESSURE_PASCAL);
        settings.formatDate(Constants.DEFAULT_FORMAT_DATE);
        settings.formatTime(Constants.FORMAT_TIME_LONG_24H);

        return settings;
    }

    private ImmutableList<Threshold> mockThresholds() {
        final List<Threshold> mocks = new ArrayList<>(0);
        IntStream.range(0, 7)
            .forEach(
                day -> {
                    final Threshold mockThreshold = new Threshold();
                    mockThreshold.day(day);
                    if (day == 0) {
                        mockThreshold.id(10L);
                        mockThreshold.color(R.color.blue_500);
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(17);
                        mockThreshold.endMinute(0);

                        mocks.add(mockThreshold);

                        mockThreshold.id(11L);
                        mockThreshold.color(R.color.red_500);
                        mockThreshold.startHour(17);
                        mockThreshold.startMinute(1);
                        mockThreshold.endHour(23);
                        mockThreshold.endMinute(59);

                        mocks.add(mockThreshold);
                    }
                    if (day == 1) {
                        mockThreshold.id(20L);
                        mockThreshold.color(R.color.grey_500);
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(17);
                        mockThreshold.endMinute(0);

                        mocks.add(mockThreshold);

                        mockThreshold.id(21L);
                        mockThreshold.color(R.color.purple_500);
                        mockThreshold.startHour(17);
                        mockThreshold.startMinute(1);
                        mockThreshold.endHour(23);
                        mockThreshold.endMinute(59);

                        mocks.add(mockThreshold);
                    }
                    if (day == 2) {
                        mockThreshold.id(30L);
                        mockThreshold.color(R.color.light_green_500);
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(17);
                        mockThreshold.endMinute(0);

                        mocks.add(mockThreshold);

                        mockThreshold.id(31L);
                        mockThreshold.color(R.color.pink_500);
                        mockThreshold.startHour(17);
                        mockThreshold.startMinute(1);
                        mockThreshold.endHour(23);
                        mockThreshold.endMinute(59);

                        mocks.add(mockThreshold);
                    }
                    if (day == 3) {
                        mockThreshold.id(40L);
                        mockThreshold.color(R.color.light_blue_500);
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(17);
                        mockThreshold.endMinute(0);

                        mocks.add(mockThreshold);

                        mockThreshold.id(41L);
                        mockThreshold.color(R.color.deep_orange_500);
                        mockThreshold.startHour(17);
                        mockThreshold.startMinute(1);
                        mockThreshold.endHour(23);
                        mockThreshold.endMinute(59);

                        mocks.add(mockThreshold);
                    }
                    if (day == 4) {
                        mockThreshold.id(50L);
                        mockThreshold.color(R.color.teal_500);
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(17);
                        mockThreshold.endMinute(0);

                        mocks.add(mockThreshold);

                        mockThreshold.id(51L);
                        mockThreshold.color(R.color.amber_500);
                        mockThreshold.startHour(17);
                        mockThreshold.startMinute(1);
                        mockThreshold.endHour(23);
                        mockThreshold.endMinute(59);

                        mocks.add(mockThreshold);
                    }
                    if (day == 5) {
                        mockThreshold.id(60L);
                        mockThreshold.color(R.color.indigo_500);
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(20);
                        mockThreshold.endMinute(0);

                        mocks.add(mockThreshold);
                    }
                    if (day == 6) {
                        mockThreshold.id(70L);
                        mockThreshold.color(R.color.green_500);
                        mockThreshold.startHour(0);
                        mockThreshold.startMinute(0);
                        mockThreshold.endHour(19);
                        mockThreshold.endMinute(0);

                        mocks.add(mockThreshold);
                    }
                }
            );
        return ImmutableList.copyOf(mocks);
    }
}
