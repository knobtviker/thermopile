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
import com.knobtviker.android.things.contrib.driver.bme680.Bme680SensorDriver;
import com.knobtviker.android.things.contrib.driver.tsl2561.TSL2561SensorDriver;
import com.knobtviker.android.things.device.RxScreenManager;
import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.models.local.Threshold;
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

    private List<Temperature> temperatureBuffer = new ArrayList<>(Constants.BUFFER_SIZE);
    private List<Humidity> humidityBuffer = new ArrayList<>(Constants.BUFFER_SIZE);
    private List<Pressure> pressureBuffer = new ArrayList<>(Constants.BUFFER_SIZE);

    @Override
    public void onCreate() {
        super.onCreate();

        initRealm();
        initStetho();
        initCalligraphy();
        initJodaTime();
        initPresenter();
        initSensors();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                if (temperatureBuffer.size() < Constants.BUFFER_SIZE) {
                    final Temperature item = new Temperature();
                    item.timestamp(DateTimeUtils.currentTimeMillis());
                    item.value(sensorEvent.values[0]);
                    item.accuracy(sensorEvent.accuracy);
                    item.vendor(sensorEvent.sensor.getVendor());
                    item.name(sensorEvent.sensor.getName());

                    temperatureBuffer.add(item);
                }
                if (temperatureBuffer.size() == Constants.BUFFER_SIZE) {
                    presenter.saveTemperatures(temperatureBuffer);
                    temperatureBuffer.clear();
                }
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
//                Log.i(TAG, "humidity: "+sensorEvent.values[0]+"");
                if (humidityBuffer.size() < Constants.BUFFER_SIZE) {
                    final Humidity item = new Humidity();
                    item.timestamp(DateTimeUtils.currentTimeMillis());
                    item.value(sensorEvent.values[0]);
                    item.accuracy(sensorEvent.accuracy);
                    item.vendor(sensorEvent.sensor.getVendor());
                    item.name(sensorEvent.sensor.getName());

                    humidityBuffer.add(item);
                }
                if (humidityBuffer.size() == Constants.BUFFER_SIZE) {
                    presenter.saveHumidities(humidityBuffer);
                    humidityBuffer.clear();
                }
                break;
            case Sensor.TYPE_PRESSURE:
//                Log.i(TAG, "pressure: "+sensorEvent.values[0]+"");
                if (pressureBuffer.size() < Constants.BUFFER_SIZE) {
                    final Pressure item = new Pressure();
                    item.timestamp(DateTimeUtils.currentTimeMillis());
                    item.value(sensorEvent.values[0]);
                    item.accuracy(sensorEvent.accuracy);
                    item.vendor(sensorEvent.sensor.getVendor());
                    item.name(sensorEvent.sensor.getName());

                    pressureBuffer.add(item);
                }
                if (pressureBuffer.size() == Constants.BUFFER_SIZE) {
                    presenter.savePressures(pressureBuffer);
                    pressureBuffer.clear();
                }
                break;
            case Sensor.TYPE_LIGHT:
//                Log.i(TAG, sensorEvent.values[0]+" lux");
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
        presenter.initScreen(160, RxScreenManager.ROTATION_180, 1800000L);
        presenter.createScreensaver();
        presenter.brightness(255);
    }

    private void initSensors() {
        registerSensorCallback();

        initBME280();
//        initBME680();
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
