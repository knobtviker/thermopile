package com.knobtviker.thermopile.presentation.presenters;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.sources.raw.RxBluetoothManager;
import com.knobtviker.thermopile.di.components.data.DaggerSettingsDataComponent;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.presentation.contracts.NetworkContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by bojan on 15/07/2017.
 */

public class NetworkPresenter extends AbstractPresenter implements NetworkContract.Presenter {

    private final NetworkContract.View view;

    private final SettingsRepository settingsRepository;

    private RxBluetoothManager rxBluetoothManager;

    public NetworkPresenter(@NonNull final Context context, @NonNull final NetworkContract.View view) {
        super(view);

        this.view = view;
        this.settingsRepository = DaggerSettingsDataComponent.create().repository();
        this.rxBluetoothManager = RxBluetoothManager.getInstance(context);
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();

        removeListeners();
    }

    @Override
    public void addListeners() {

    }

    @Override
    public void removeListeners() {

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
    public void setBluetoothDeviceClass(final int service, final int device) {
        compositeDisposable.add(
            rxBluetoothManager
                .setDeviceClass(service, device)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::completed,
                    this::error
                )
        );
    }

    @Override
    public void setBluetoothProfile(final int profile) {
        compositeDisposable.add(
            rxBluetoothManager
                .setProfile(BluetoothProfile.GATT_SERVER)
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