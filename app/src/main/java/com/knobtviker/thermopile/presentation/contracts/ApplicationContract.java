package com.knobtviker.thermopile.presentation.contracts;

import android.content.Context;
import android.hardware.SensorEvent;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.knobtviker.android.things.contrib.community.boards.I2CDevice;
import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by bojan on 15/07/2017.
 */

public interface ApplicationContract {

    interface View extends BaseView {

        void onTemperatureChanged(@NonNull final SensorEvent sensorEvent);

        void onPressureChanged(@NonNull final SensorEvent sensorEvent);

        void onHumidityChanged(@NonNull final SensorEvent sensorEvent);

        void onAirQualityChanged(@NonNull final SensorEvent sensorEvent);

        void onLuminosityChanged(@NonNull final SensorEvent sensorEvent);

        void onAccelerationChanged(@NonNull final SensorEvent sensorEvent);

        void onAngularVelocityChanged(@NonNull final SensorEvent sensorEvent);

        void onMagneticFieldChanged(@NonNull final SensorEvent sensorEvent);

        void onSettings(@NonNull final Settings settings);

        void onPeripherals(@NonNull final RealmResults<PeripheralDevice> peripheralDevices);

        void showScreensaver();
    }

    interface Presenter extends BasePresenter {

        void observeSensors(@NonNull final Context context);

        void observeTemperatureChanged(@NonNull final Context context);

        void observePressureChanged(@NonNull final Context context);

        void observeAltitudeChanged(@NonNull final Context context);

        void observeHumidityChanged(@NonNull final Context context);

        void observeAirQualityChanged(@NonNull final Context context);

        void observeLuminosityChanged(@NonNull final Context context);

        void observeAccelerationChanged(@NonNull final Context context);

        void observeAngularVelocityChanged(@NonNull final Context context);

        void observeMagneticFieldChanged(@NonNull final Context context);

        void createScreensaver();

        void destroyScreensaver();

        void initScreen(final int density, final int rotation);

        void brightness(final int brightness);

        void settings(@NonNull final Realm realm);

        void peripherals(@NonNull final Realm realm);

        ImmutableList<I2CDevice> i2cDevices();

        RealmResults<PeripheralDevice> defaultSensors(@NonNull final Realm realm);

        void saveDefaultSensors(@NonNull final List<PeripheralDevice> foundSensors);
    }
}
