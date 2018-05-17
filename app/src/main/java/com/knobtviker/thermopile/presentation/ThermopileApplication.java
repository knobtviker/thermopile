package com.knobtviker.thermopile.presentation;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.ApplicationContract;
import com.knobtviker.thermopile.presentation.presenters.ApplicationPresenter;
import com.knobtviker.thermopile.presentation.utils.Router;

import timber.log.Timber;

/**
 * Created by bojan on 15/07/2017.
 */

// /data/data/com.knobtviker.thermopile
public class ThermopileApplication extends AbstractApplication<ApplicationContract.Presenter> implements ApplicationContract.View {
    private static final String TAG = ThermopileApplication.class.getSimpleName();

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
    public void onSettings(@NonNull Settings settings) {
        AppCompatDelegate.setDefaultNightMode(settings.theme);
    }

//    @Override
//    public void onSensorFound(int address) {
//        Timber.i("Sensor address found %d", address);
//        try {
//            //TODO: Move these hardcoded addresses to some Integrity with default constants class
//            switch (address) {
//                case 0x77:
//                    initBME280();
//                    break;
//                case 0x76:
//                    initBME680();
//                    break;
//                case 0x68:
//                    initDS3231();
//                    break;
//                case 0x39:
//                    initTSL2561();
//                    break;
//                case 0x6B:
//                    initLSM9DS1();
//                    break;
//            }
//        } catch (IOException e) {
//            showError(e);
//        }
//    }

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

//        presenter.peripherals();
    }

//    private void initFram() {
//        try {
//            final Mb85rc256v fram = new Mb85rc256v(BoardDefaults.defaultI2CBus());
//            fram.setVerbose(true);
//            final int rebootCount = fram.readInt(0);
//            fram.writeInt(0, rebootCount +1);
//            fram.close();
//        } catch (IOException e) {
//            Timber.e(e);
//        }
//    }

    private boolean isThingsDevice(@NonNull final Context context) {
        return context
            .getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_EMBEDDED);
    }
}
