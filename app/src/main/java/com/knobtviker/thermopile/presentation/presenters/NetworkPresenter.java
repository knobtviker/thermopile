package com.knobtviker.thermopile.presentation.presenters;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.sources.raw.RxBluetoothManager;
import com.knobtviker.thermopile.di.components.data.DaggerPeripheralsDataComponent;
import com.knobtviker.thermopile.di.components.data.DaggerSettingsDataComponent;
import com.knobtviker.thermopile.domain.repositories.PeripheralsRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.NetworkContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by bojan on 15/07/2017.
 */

public class NetworkPresenter extends AbstractPresenter implements NetworkContract.Presenter {

    private final NetworkContract.View view;

    private final PeripheralsRepository peripheralsRepository;
    private final SettingsRepository settingsRepository;

    private final RxBluetoothManager rxBluetoothManager;

    public NetworkPresenter(@NonNull final Context context, @NonNull final NetworkContract.View view) {
        super(view);

        this.view = view;
        this.peripheralsRepository = DaggerPeripheralsDataComponent.create().repository();
        this.settingsRepository = DaggerSettingsDataComponent.create().repository();
        this.rxBluetoothManager = RxBluetoothManager.getInstance(context);
    }

    @Override
    public void observeTemperatureChanged(@NonNull Context context) {
        compositeDisposable.add(
            peripheralsRepository
                .observeTemperature(context)
                .subscribe(
                    view::onTemperatureChanged,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void observePressureChanged(@NonNull Context context) {
        compositeDisposable.add(
            peripheralsRepository
                .observePressure(context)
                .subscribe(
                    view::onPressureChanged,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void observeHumidityChanged(@NonNull Context context) {
        compositeDisposable.add(
            peripheralsRepository
                .observeHumidity(context)
                .subscribe(
                    view::onHumidityChanged,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void observeAirQualityChanged(@NonNull Context context) {
        compositeDisposable.add(
            peripheralsRepository
                .observeAirQuality(context)
                .subscribe(
                    view::onAirQualityChanged,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void observeAccelerationChanged(@NonNull Context context) {
        compositeDisposable.add(
             peripheralsRepository
                 .observeAcceleration(context)
                .subscribe(
                    view::onAccelerationChanged,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void hasBluetooth() {
        compositeDisposable.add(
            Observable
                .zip(
                    rxBluetoothManager.hasBluetooth(),
                    rxBluetoothManager.hasBluetoothLowEnergy(),
                    (hasBluetooth, hasBle) -> hasBluetooth && hasBle
                )
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    view::onHasBluetooth,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void bluetooth(boolean enable) {
        compositeDisposable.add(
            (enable ? rxBluetoothManager.enable() : rxBluetoothManager.disable())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void isBluetoothEnabled() {
        compositeDisposable.add(
            rxBluetoothManager
                .isEnabled()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    view::onBluetoothEnabled,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void name(@NonNull String name) {
        compositeDisposable.add(
            rxBluetoothManager
                .name(name)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void discoverable(@NonNull Activity activity, int requestCode, int duration) {
        rxBluetoothManager.enableDiscoverability(activity, 8008, 300);
    }

    @Override
    public void observeBluetoothState() {
        compositeDisposable.add(
            rxBluetoothManager
                .state()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    state -> {
                        switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                                view.onBluetoothState(false);
                                break;
                            case BluetoothAdapter.STATE_ON:
                                view.onBluetoothState(true);
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                            case BluetoothAdapter.STATE_TURNING_ON:
                                view.onBluetoothStateIndeterminate();
                                break;
                        }
                    }
                )
        );
    }

    @Override
    public void setBluetoothDeviceClass(final int service, final int device, final int ioCapability) {
        compositeDisposable.add(
            rxBluetoothManager
                .setDeviceClass(service, device, ioCapability)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void setBluetoothProfiles(@NonNull final List<Integer> profiles) {
        compositeDisposable.add(
            rxBluetoothManager
                .setProfiles(profiles)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void startGattServer(@NonNull final BluetoothGattServerCallback callback) {
        compositeDisposable.add(
            rxBluetoothManager
                .startGattServer(callback)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    view::onGattServerStarted,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void stopGattServer() {
        compositeDisposable.add(
            rxBluetoothManager
                .stopGattServer()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void isGattServerRunning() {
        compositeDisposable.add(
            rxBluetoothManager
                .isGattServerRunning()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    view::onCheckGattServer,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void startBluetoothAdvertising(@NonNull AdvertiseSettings settings, @NonNull AdvertiseData data, @NonNull AdvertiseCallback callback) {
        compositeDisposable.add(
            rxBluetoothManager
                .startAdvertising(settings, data, callback)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void stopBluetoothAdvertising() {
        compositeDisposable.add(
            rxBluetoothManager
                .stopAdvertising()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void isBluetoothAdvertising() {
        compositeDisposable.add(
            rxBluetoothManager
                .isAdvertising()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    view::onCheckBluetoothAdvertising,
                    this::error,
                    this::completed
                )
        );
    }
}
