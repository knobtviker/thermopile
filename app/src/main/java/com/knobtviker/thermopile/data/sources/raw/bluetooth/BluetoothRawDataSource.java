package com.knobtviker.thermopile.data.sources.raw.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.google.android.things.bluetooth.BluetoothClassFactory;
import com.google.android.things.bluetooth.BluetoothConfigManager;
import com.google.android.things.bluetooth.BluetoothProfile;
import com.google.android.things.bluetooth.BluetoothProfileManager;

import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.MainThreadDisposable;

import static android.content.Context.BLUETOOTH_SERVICE;

public class BluetoothRawDataSource {

    public final static int MAX_DISCOVERABILITY_PERIOD_SECONDS = 300;

    private final Context context;
    private final BluetoothAdapter bluetoothAdapter;
    private final BluetoothManager bluetoothManager;

    @Nullable
    private BluetoothGattServer gattServer;

    private BluetoothLeAdvertiser bluetoothLeAdvertiser;

    @Nullable
    private AdvertiseCallback advertiseCallback;

    private boolean isGattServerRunning = false;
    private boolean isAdvertising = false;

    public BluetoothRawDataSource(@NonNull final Context context) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
        this.bluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
    }

    /**
     * Return true if Bluetooth is available.
     *
     * @return true if bluetoothAdapter is not null or it's address is empty,
     * otherwise Bluetooth is not supported on this hardware
     */
    @SuppressLint("HardwareIds")
    public Observable<Boolean> hasBluetooth() {
        return Observable.defer(() -> Observable.just(!(bluetoothAdapter == null || TextUtils.isEmpty(bluetoothAdapter.getAddress()))));
    }

    /**
     * Return true if Bluetooth LE is available.
     *
     * @return true if bluetoothAdapter is not null or it's address is empty and system has feature,
     * otherwise Bluetooth LE is not supported on this hardware
     */
    public Observable<Boolean> hasBluetoothLowEnergy() {
        return Observable.defer(() -> Observable.just(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)));
    }

    /**
     * Return true if Bluetooth is currently enabled and ready for use.
     *
     * @return true if the local adapter is turned on
     */
    public Observable<Boolean> isEnabled() {
        return Observable.defer(() -> Observable.just(bluetoothAdapter.isEnabled()));
    }

    /**
     * Turn on the local Bluetooth adapter
     *
     * @return true to indicate adapter startup has begun, or false immediate error
     */
    public Completable enable() {
        return Completable.defer(() ->
            Completable.create(
                emitter -> {
                    if (!emitter.isDisposed()) {
                        if (bluetoothAdapter.enable()) {
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Throwable("Cannot turn on Bluetooth adapter"));
                        }
                    }
                }
            )
        );
    }

    /**
     * Turn off the local Bluetooth adapter
     *
     * @return true to indicate adapter shutdown has begun, or false on immediate error
     */
    public Completable disable() {
        return Completable.defer(() ->
            Completable.create(
                emitter -> {
                    if (!emitter.isDisposed()) {
                        if (bluetoothAdapter.disable()) {
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Throwable("Cannot turn off Bluetooth adapter"));
                        }
                    }
                }
            )
        );
    }

    /**
     * Observes BluetoothState. Possible values are:
     * {@link BluetoothAdapter#STATE_OFF},
     * {@link BluetoothAdapter#STATE_TURNING_ON},
     * {@link BluetoothAdapter#STATE_ON},
     * {@link BluetoothAdapter#STATE_TURNING_OFF},
     */
    public Observable<Integer> state() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        return Observable.defer(() ->
            Observable.create(emitter -> {
                final BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.hasExtra(BluetoothAdapter.EXTRA_STATE)) {
                            emitter.onNext(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF));
                        }
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

    public Completable name(@StringRes final int resId) {
        return Completable.defer(() ->
            Completable.create(emitter -> {
                if (!emitter.isDisposed()) {
                    if (bluetoothAdapter != null) {
                        bluetoothAdapter.setName(context.getString(resId));
                        emitter.onComplete();
                    } else {
                        emitter.onError(new Throwable("BluetoothAdapter cannot be null or not running."));
                    }
                }
            })
        );
    }

    public Completable setDeviceClass(final int service, final int device, final int ioCapability) {
        return Completable.defer(() ->
            Completable.create(emitter -> {
                    if (!emitter.isDisposed()) {
                        final BluetoothConfigManager configManager = BluetoothConfigManager.getInstance();
                        configManager.setIoCapability(ioCapability);
                        configManager.setLeIoCapability(ioCapability);

                        final BluetoothClass deviceClass = BluetoothClassFactory.build(service, device);

                        if (configManager.setBluetoothClass(deviceClass)) {
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Throwable("Cannot set Bluetooth class service and device"));
                        }
                    }
                }
            )
        );
    }

    public Completable setProfiles(@NonNull final List<Integer> profiles) {
        return Completable.defer(() ->
            Completable.create(
                emitter -> {
                    if (!emitter.isDisposed()) {
                        if (profiles.contains(BluetoothProfile.A2DP) && profiles.contains(BluetoothProfile.A2DP_SINK)) {
                            emitter.onError(new Throwable("A2DP and A2DP_SINK profiles cannot be enabled together."));
                        } else {
                            if (!profiles.contains(BluetoothProfile.GATT)) {
                                profiles.add(BluetoothProfile.GATT);
                            }
                            if (!profiles.contains(BluetoothProfile.GATT_SERVER)) {
                                profiles.add(BluetoothProfile.GATT_SERVER);
                            }

                            final BluetoothProfileManager profileManager = BluetoothProfileManager.getInstance();
                            profileManager.setEnabledProfiles(profiles);
                            emitter.onComplete();
                        }
                    }
                }
            )
        );
    }

    /**
     * Initialize the GATT server instance getInstance callback.
     */
    public Completable startGattServer(@NonNull final BluetoothGattServerCallback bluetoothGattServerCallback) {
        return Completable.defer(() ->
            Completable.create(emitter -> {
                if (!emitter.isDisposed()) {
                    this.gattServer = bluetoothManager.openGattServer(context, bluetoothGattServerCallback);
                    this.isGattServerRunning = gattServer != null;

                    if (gattServer == null) {
                        emitter.onError(new Throwable("Gatt server cannot start."));
                    }

                    emitter.onComplete();
                }
            })
        );
    }

    /**
     * Shut down the GATT server.
     */
    public Completable stopGattServer() {
        return Completable.defer(() ->
            Completable.create(emitter -> {
                if (!emitter.isDisposed()) {
                    if (gattServer != null && isGattServerRunning) {
                        gattServer.close();
                        gattServer = null;
                        isGattServerRunning = false;
                    }
                    emitter.onComplete();
                }
            })
        );
    }

    /**
     * Begin advertising over Bluetooth that this device is connectable and supports the selected service.
     */
    public Completable startAdvertising() {
        return Completable.defer(() ->
            Completable.create(emitter -> {
                    if (!emitter.isDisposed()) {

                        final AdvertiseSettings settings = advertiseSettings();
                        final AdvertiseData data = advertiseData();

                        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
                        if (bluetoothLeAdvertiser != null) {
                            advertiseCallback = new AdvertiseCallback() {
                                @Override
                                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                                    super.onStartSuccess(settingsInEffect);

                                    isAdvertising = true;

                                    emitter.onComplete();
                                }

                                @Override
                                public void onStartFailure(int errorCode) {
                                    super.onStartFailure(errorCode);

                                    isAdvertising = false;
                                    advertiseCallback = null;

                                    emitter.onError(
                                        new Throwable(String.format(Locale.getDefault(), "Advertising error: %d", errorCode))
                                    );
                                }
                            };

                            bluetoothLeAdvertiser.startAdvertising(settings, data, advertiseCallback);
                        } else {
                            emitter.onError(new Throwable("Bluetooth LE Advertising is not supported on this device."));
                        }
                    }
                }
            )
        );
    }

    /**
     * Stop Bluetooth advertisements.
     */
    public Completable stopAdvertising() {
        return Completable.defer(() ->
            Completable.create(emitter -> {
                if (!emitter.isDisposed()) {
                    if (bluetoothLeAdvertiser != null) {
                        bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
                    }
                    emitter.onComplete();
                }
            })
        );
    }

    /**
     * This will issue a request to make the local device discoverable to other devices. By default,
     * the device will become discoverable for 120 seconds.
     *
     * @param activity    Activity
     * @param requestCode request code
     */
    public void enableDiscoverability(@NonNull final Activity activity, final int requestCode) {
        enableDiscoverability(activity, requestCode, -1);
    }

    /**
     * This will issue a request to make the local device discoverable to other devices. By default,
     * the device will become discoverable for 120 seconds.  Maximum duration is capped at 300
     * seconds.
     *
     * @param activity    Activity
     * @param requestCode request code
     * @param duration    discoverability duration in seconds
     */
    public void enableDiscoverability(@NonNull final Activity activity, final int requestCode, final int duration) {
        final Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        if (duration >= 0) {
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                duration > MAX_DISCOVERABILITY_PERIOD_SECONDS ? MAX_DISCOVERABILITY_PERIOD_SECONDS : duration);
        }
        activity.startActivityForResult(discoverableIntent, requestCode);
    }

    private AdvertiseSettings advertiseSettings() {
        return new AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setConnectable(true)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .build();
    }

    private AdvertiseData advertiseData() {
        return new AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .setIncludeTxPowerLevel(false)
            .addServiceUuid(new ParcelUuid(TimeProfile.TIME_SERVICE))
            .addServiceUuid(new ParcelUuid(EnvironmentProfile.ENVIRONMENT_SENSING_SERVICE))
            .build();
    }
}
