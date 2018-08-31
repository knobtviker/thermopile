package com.knobtviker.thermopile.presentation;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.presenters.ApplicationPresenter;
import com.knobtviker.thermopile.presentation.shared.base.AbstractApplication;
import com.knobtviker.thermopile.presentation.utils.Router;

import timber.log.Timber;

/**
 * Created by bojan on 15/07/2017.
 */

// /data/data/com.knobtviker.thermopile
public class ThermopileApplication extends AbstractApplication<ApplicationContract.Presenter> implements ApplicationContract.View {

    private static long lastBootTimestamp = 0L;

    private static long bootCount = 1L;

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

    public static long lastBootTimestamp() {
        return lastBootTimestamp;
    }

    public static long bootCount() {
        return bootCount;
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
    }

    private boolean isThingsDevice(@NonNull final Context context) {
        return context
            .getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_EMBEDDED);
    }
}
