package com.knobtviker.thermopile.presentation.contracts;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.shared.base.BasePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseView;

/**
 * Created by bojan on 15/07/2017.
 */

public interface NetworkContract {

    interface View extends BaseView {

        void onTemperatureChanged(final float value);

        void onPressureChanged(final float value);

        void onHumidityChanged(final float value);

        void onAirQualityChanged(final float value);

        void onAccelerationChanged(final float[] values);

        void onHasBluetooth(final boolean hasBluetooth);

        void onIsBluetoothEnabled(final boolean isEnabled);

        void onBluetoothState(final boolean isOn);

        void onBluetoothStateIndeterminate();

        void onSetupCompleted();
    }

    interface Presenter extends BasePresenter {

        void observeTemperature();

        void observePressure();

        void observeHumidity();

        void observeAirQuality();

        void observeAcceleration();

        void hasBluetooth();

        void isBluetoothEnabled();

        void enableBluetooth();

        void disableBluetooth();

        void observeBluetoothState();

        void startGattServer();

        void stopGattServer();

        void startBluetoothAdvertising();

        void stopBluetoothAdvertising();

        void enableDiscoverability(@NonNull final Activity activity, int requestCode);
    }
}
