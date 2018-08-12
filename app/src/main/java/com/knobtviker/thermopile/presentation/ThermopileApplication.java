package com.knobtviker.thermopile.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDelegate;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.presenters.ApplicationPresenter;
import com.knobtviker.thermopile.presentation.shared.base.AbstractApplication;
import com.knobtviker.thermopile.presentation.utils.Router;
import com.knobtviker.thermopile.shared.constants.BluetoothState;
import com.knobtviker.thermopile.shared.constants.Keys;

import timber.log.Timber;

/**
 * Created by bojan on 15/07/2017.
 */

// /data/data/com.knobtviker.thermopile
public class ThermopileApplication extends AbstractApplication<ApplicationContract.Presenter> implements ApplicationContract.View {
    private static final String TAG = ThermopileApplication.class.getSimpleName();

    private static long lastBootTimestamp = 0L;

    private static long bootCount = 1L;

    private static boolean hasBluetooth = false;

    private static boolean isBluetoothEnabled = false;

    @BluetoothState
    private int bluetoothState;

    @Override
    public void onCreate() {
        super.onCreate();

        initPresenter();
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);
    }

    @Override
    public void onSettingsChanged(@NonNull Settings settings) {
        if (AppCompatDelegate.getDefaultNightMode() != settings.theme) {
            AppCompatDelegate.setDefaultNightMode(settings.theme);
        }
    }

    @Override
    public void showScreensaver() {
        Router.showScreensaver(this);
    }

    @Override
    public void onNewTemperature(@NonNull String vendor, @NonNull String name, float value) {
        presenter.saveTemperature(vendor, name, value);
    }

    @Override
    public void onNewPressure(@NonNull String vendor, @NonNull String name, float value) {
        presenter.savePressure(vendor, name, value);
    }

    @Override
    public void onNewHumidity(@NonNull String vendor, @NonNull String name, float value) {
        presenter.saveHumidity(vendor, name, value);
    }

    @Override
    public void onNewAirQuality(@NonNull String vendor, @NonNull String name, float value) {
        presenter.saveAirQuality(vendor, name, value);
    }

    @Override
    public void onNewLuminosity(@NonNull String vendor, @NonNull String name, float value) {
        presenter.saveLuminosity(vendor, name, value);
    }

    @Override
    public void onNewAcceleration(@NonNull String vendor, @NonNull String name, float[] values) {
        presenter.saveAcceleration(vendor, name, values);
    }

    @Override
    public void onNewAngularVelocity(@NonNull String vendor, @NonNull String name, float[] values) {
        presenter.saveAngularVelocity(vendor, name, values);
    }

    @Override
    public void onNewMagneticField(@NonNull String vendor, @NonNull String name, float[] values) {
        presenter.saveMagneticField(vendor, name, values);
    }

    @Override
    public void onLastBootTimestamp(long value) {
        lastBootTimestamp = value;
    }

    @Override
    public void onBootCount(long value) {
        bootCount = value;
    }

    @Override
    public void onHasBluetooh(boolean value) {
        hasBluetooth = value;
    }

    @Override
    public void onBluetoothEnabled(boolean value) {
        isBluetoothEnabled = value;
        if (value) {
            Router.enableDiscoverability(this, Router.MAX_DISCOVERABILITY_PERIOD_SECONDS);
        }
    }

    @Override
    public void onBluetoothState(@BluetoothState int value) {
        bluetoothState = value;

        final Intent intent = new Intent();
        intent.putExtra(Keys.BLUETOOTH_STATE, bluetoothState);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public static long lastBootTimestamp() {
        return lastBootTimestamp;
    }

    public static long bootCount() {
        return bootCount;
    }

    public static boolean hasBluetooth() {
        return hasBluetooth;
    }

    public static boolean isBluetoothEnabled() {
        return isBluetoothEnabled;
    }

    public void createScreensaver() {
        presenter.createScreensaver();
    }

    public void destroyScreensaver() {
        presenter.destroyScreensaver();
    }

    private void initPresenter() {
        presenter = new ApplicationPresenter(this);

        presenter.createScreensaver();

        presenter.settings();
    }

    private boolean isThingsDevice(@NonNull final Context context) {
        return context
            .getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_EMBEDDED);
    }
}
