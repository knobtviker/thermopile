package com.knobtviker.thermopile.presentation;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.bugfender.sdk.Bugfender;
import com.facebook.stetho.Stetho;
import com.google.android.things.device.TimeManager;
import com.knobtviker.android.things.contrib.community.boards.BoardDefaults;
import com.knobtviker.android.things.contrib.community.driver.bme280.BME280SensorDriver;
import com.knobtviker.android.things.contrib.community.driver.bme680.Bme680;
import com.knobtviker.android.things.contrib.community.driver.bme680.Bme680SensorDriver;
import com.knobtviker.android.things.contrib.community.driver.ds3231.Ds3231;
import com.knobtviker.android.things.contrib.community.driver.ds3231.Ds3231SensorDriver;
import com.knobtviker.android.things.contrib.community.driver.lsm9ds1.Lsm9ds1SensorDriver;
import com.knobtviker.android.things.contrib.community.driver.tsl2561.TSL2561SensorDriver;
import com.knobtviker.android.things.contrib.community.support.rxscreenmanager.RxScreenManager;
import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.data.models.local.Acceleration;
import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.Altitude;
import com.knobtviker.thermopile.data.models.local.AngularVelocity;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.MagneticField;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.models.presentation.Atmosphere;
import com.knobtviker.thermopile.data.sources.local.implemenatation.Database;
import com.knobtviker.thermopile.presentation.activities.MainActivity;
import com.knobtviker.thermopile.presentation.activities.ScreenSaverActivity;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.fragments.MainFragment;
import com.knobtviker.thermopile.presentation.fragments.ScreensaverFragment;
import com.knobtviker.thermopile.presentation.presenters.ApplicationPresenter;
import com.knobtviker.thermopile.presentation.utils.Router;
import com.knobtviker.thermopile.presentation.views.listeners.ApplicationState;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTimeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by bojan on 15/07/2017.
 */

// /data/data/com.knobtviker.thermopile
public class ThermopileApp extends Application implements SensorEventListener, ApplicationContract.View, ApplicationState.Listener {
    private static final String TAG = ThermopileApp.class.getSimpleName();

    @Nullable
    private Activity currentActivity;

    @NonNull
    private ApplicationContract.Presenter presenter;

    private Atmosphere atmosphere = Atmosphere.EMPTY();

    private ConcurrentLinkedQueue<Temperature> temperatures = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Pressure> pressures = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Humidity> humidities = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Altitude> altitudes = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<AirQuality> airQualities = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Acceleration> accelerations = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<AngularVelocity> angularVelocities = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<MagneticField> magneticFields = new ConcurrentLinkedQueue<>();

    @Override
    public void onCreate() {
        super.onCreate();

        test();

        initBugfender();
        initApplicationState();
        initSensors();
        initDatabase();
        initStetho();
        initJodaTime();
        initPresenter();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                atmosphere = atmosphere.withTemperature(sensorEvent.values[0]);

                temperatures.add(new Temperature(DateTimeUtils.currentTimeMillis(), sensorEvent.values[0], sensorEvent.accuracy, sensorEvent.sensor.getVendor(), sensorEvent.sensor.getName()));
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                atmosphere = atmosphere.withHumidity(sensorEvent.values[0]);

                humidities.add(new Humidity(DateTimeUtils.currentTimeMillis(), sensorEvent.values[0], sensorEvent.accuracy, sensorEvent.sensor.getVendor(), sensorEvent.sensor.getName()));
                break;
            case Sensor.TYPE_PRESSURE:
                final long now = DateTimeUtils.currentTimeMillis();

                atmosphere = atmosphere.withPressure(sensorEvent.values[0]);

                pressures.add(new Pressure(now, sensorEvent.values[0], sensorEvent.accuracy, sensorEvent.sensor.getVendor(), sensorEvent.sensor.getName()));

                final float altitudeValue = SensorManager.getAltitude(sensorEvent.values[0], SensorManager.PRESSURE_STANDARD_ATMOSPHERE);

                atmosphere = atmosphere.withAltitude(altitudeValue);

                altitudes.add(new Altitude(now, altitudeValue, sensorEvent.accuracy, sensorEvent.sensor.getVendor(), sensorEvent.sensor.getName()));
                break;
            case Sensor.TYPE_DEVICE_PRIVATE_BASE:
                if (sensorEvent.sensor.getStringType().equals(Bme680.CHIP_SENSOR_TYPE_IAQ)) {
                    if (sensorEvent.sensor.getName().equals(Bme680.CHIP_NAME)) {
                        atmosphere = atmosphere.withAirQuality(sensorEvent.values[Bme680SensorDriver.INDOOR_AIR_QUALITY_INDEX]);

                        airQualities.add(new AirQuality(DateTimeUtils.currentTimeMillis(), sensorEvent.values[Bme680SensorDriver.INDOOR_AIR_QUALITY_INDEX], sensorEvent.accuracy, sensorEvent.sensor.getVendor(), sensorEvent.sensor.getName()));
                    }
                }
                break;
            case Sensor.TYPE_LIGHT:
                //TODO: Google dropped Automatic Brightness Mode in DP7. Do your own math with manual mode. Less light == lower screen brightness.
//                Log.i(TAG, "Measured: " + sensorEvent.values[0] + " lx --- Fitted: " + TSL2561SensorDriver.getFittedLuminosity(sensorEvent.values[0])+ " lx --- Screen brightness: " + TSL2561SensorDriver.getScreenBrightness(sensorEvent.values[0]));
                break;
            case Sensor.TYPE_ACCELEROMETER: //[m/s^2]
                atmosphere = atmosphere.withAcceleration(new float[]{sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]});

                accelerations.add(new Acceleration(DateTimeUtils.currentTimeMillis(), sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], sensorEvent.accuracy, sensorEvent.sensor.getVendor(), sensorEvent.sensor.getName()));
                break;
            case Sensor.TYPE_GYROSCOPE: //[°/s]
                atmosphere = atmosphere.withAngularVelocity(new float[]{sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]});

                angularVelocities.add(new AngularVelocity(DateTimeUtils.currentTimeMillis(), sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], sensorEvent.accuracy, sensorEvent.sensor.getVendor(), sensorEvent.sensor.getName()));
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                atmosphere = atmosphere.withMagneticField(new float[]{sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]});

                magneticFields.add(new MagneticField(DateTimeUtils.currentTimeMillis(), sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], sensorEvent.accuracy, sensorEvent.sensor.getVendor(), sensorEvent.sensor.getName()));
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
            case Sensor.TYPE_ACCELEROMETER:
                break;
            case Sensor.TYPE_GYROSCOPE:
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
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
    public void onSettings(@NonNull Settings settings) {
        AppCompatDelegate.setDefaultNightMode(settings.theme());
    }

    @Override
    public void onTick() {
        save();
    }

    @Override
    public void showScreensaver() {
        Router.showScreensaver(this);
        brightness(24);
    }

    @Override
    public void onForeground() {
        //DO NOTHING
    }

    @Override
    public void onBackground() {
        //DO NOTHING
    }

    @Override
    public void onMainActivityShown(@NonNull MainActivity activity) {
        this.currentActivity = activity;

        populateFragment();
    }

    @Override
    public void onScreensaverActivityShown(@NonNull ScreenSaverActivity activity) {
        this.currentActivity = activity;

        populateFragment();
    }

    @Override
    public void onMainActivityHidden() {
        this.currentActivity = null;
    }

    @Override
    public void onScreensaverActivityHidden() {
        this.currentActivity = null;
    }

    public Atmosphere atmosphere() {
        return atmosphere;
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
        presenter.initScreen(160, RxScreenManager.ROTATION_180);
        presenter.createScreensaver();
        presenter.brightness(255);
        presenter.settings(Database.getDefaultInstance());
    }

    private void initBugfender() {
        Bugfender.init(this, BuildConfig.KEY_BUGFENDER, BuildConfig.DEBUG);
        Bugfender.enableLogcatLogging();
        Bugfender.enableUIEventLogging(this);
        Bugfender.enableCrashReporting();
    }

    private void initApplicationState() {
        ApplicationState.init(this);
    }

    private void initSensors() {
        registerSensorCallback();

        try {
            initBME280();
            initBME680();
            initDS3231();
            initTSL2561();
            initLSM9DS1();

//            final Mb85rc256v fram = new Mb85rc256v(BoardDefaults.getI2CPort());
//            fram.dump();
//            fram.close();

        } catch (IOException e) {
            showError(e);
        }
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
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_MAGNETIC_FIELD:
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

    private boolean isThingsDevice(@NonNull final Context context) {
        return context
            .getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_EMBEDDED);
    }

    private void populateFragment() {
        if (currentActivity != null) {
            if (currentActivity instanceof MainActivity) {
                currentActivity.runOnUiThread(() ->
                    ((MainFragment) ((MainActivity) currentActivity).mainFragment()).setAtmosphere(atmosphere)
                );
            } else if (currentActivity instanceof ScreenSaverActivity) {
                currentActivity.runOnUiThread(() ->
                    ((ScreensaverFragment) ((ScreenSaverActivity) currentActivity).screensaverFragment()).setAtmosphere(atmosphere)
                );
            }
        }
    }

    private void save() {
        atmosphere = atmosphere.withTimestamp(DateTimeUtils.currentTimeMillis());

        populateFragment();

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
        if (!accelerations.isEmpty()) {
            presenter.saveAccelerations(new ArrayList<>(accelerations));
            accelerations.clear();
        }
        if (!angularVelocities.isEmpty()) {
            presenter.saveAngularVelocities(new ArrayList<>(angularVelocities));
            angularVelocities.clear();
        }
        if (!magneticFields.isEmpty()) {
            presenter.saveMagneticFields(new ArrayList<>(magneticFields));
            magneticFields.clear();
        }
    }

    private void test() {
        BoardDefaults
            .i2CDevices()
            .forEach(device -> Log.i(TAG, device.bus() + " --- " + device.address() + " --- " + device.addressHex()));
    }
}
