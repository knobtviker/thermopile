package com.knobtviker.thermopile.data.sources.raw;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.MainThreadDisposable;

public class RxBluetoothManager {

    private final BluetoothAdapter bluetoothAdapter;
    private final Context context;

    public static RxBluetoothManager with(@NonNull final Context context) {
        return new RxBluetoothManager(context);
    }

    public RxBluetoothManager(@NonNull final Context context) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    }

    /**
     * Return true if Bluetooth is available.
     *
     * @return true if bluetoothAdapter is not null or it's address is empty,
     * otherwise Bluetooth is not supported on this hardware
     */
    public boolean hasBluetooth() {
        return !(bluetoothAdapter == null || TextUtils.isEmpty(bluetoothAdapter.getAddress()));
    }

    /**
     * Return true if Bluetooth LE is available.
     *
     * @return true if bluetoothAdapter is not null or it's address is empty and system has feature,
     * otherwise Bluetooth LE is not supported on this hardware
     */
    public boolean hasBluetoothLowEnergy() {
        return hasBluetooth() && context.getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * Return true if Bluetooth is currently enabled and ready for use.
     *
     * @return true if the local adapter is turned on
     */
    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    /**
     * Turn on the local Bluetooth adapter
     *
     * @return true to indicate adapter startup has begun, or false immediate error
     */
    public boolean enable() {
        return bluetoothAdapter.enable();
    }

    /**
     * Turn off the local Bluetooth adapter
     *
     * @return true to indicate adapter shutdown has begun, or false on immediate error
     */
    public boolean disable() {
        return bluetoothAdapter.disable();
    }

    /**
     * Observes BluetoothState. Possible values are:
     * {@link BluetoothAdapter#STATE_OFF},
     * {@link BluetoothAdapter#STATE_TURNING_ON},
     * {@link BluetoothAdapter#STATE_ON},
     * {@link BluetoothAdapter#STATE_TURNING_OFF},
     *
     * @return RxJava2 Observable with BluetoothState
     */
    public Observable<Integer> state() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        return Observable.defer(() ->
            Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
                final BroadcastReceiver receiver = new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        emitter.onNext(bluetoothAdapter.getState());
                    }
                };

                context.registerReceiver(receiver, filter);

                emitter.setDisposable(new MainThreadDisposable() {
                    @Override
                    protected void onDispose() {
                        context.unregisterReceiver(receiver);

                        dispose();
                    }
                });
            }));
    }
}
