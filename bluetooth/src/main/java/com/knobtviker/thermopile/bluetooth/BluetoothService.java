package com.knobtviker.thermopile.bluetooth;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.things.bluetooth.BluetoothClassFactory;
import com.google.android.things.bluetooth.BluetoothConfigManager;
import com.google.android.things.bluetooth.BluetoothProfileManager;
import com.knobtviker.thermopile.shared.MessageFactory;
import com.knobtviker.thermopile.shared.constants.Action;
import com.knobtviker.thermopile.shared.constants.BluetoothState;
import com.knobtviker.thermopile.shared.constants.Keys;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class BluetoothService extends Service {

    public final static int MAX_DISCOVERABILITY_PERIOD_SECONDS = 300;

    @NonNull
    private IncomingHandler incomingHandler;

    @NonNull
    private Messenger serviceMessenger;

    @Nullable
    private static Messenger foregroundMessenger = null;

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothManager bluetoothManager;

    @Nullable
    private BroadcastReceiver stateReceiver;

    @Nullable
    private BluetoothGattServer gattServer;

    private BluetoothLeAdvertiser bluetoothLeAdvertiser;

    @Nullable
    private AdvertiseCallback advertiseCallback;

    @BluetoothState
    private int bluetoothState = BluetoothState.OFF;

    private boolean isBluetoothRunning = false;
    private boolean isGattServerRunning = false;
    private boolean isAdvertising = false;

    @Override
    public void onCreate() {
        super.onCreate();
        plantTree();
        setupMessenger();
        setupDriver();
        registerStateReceiver();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterStateReceiver();
        destroyDriver();
        super.onDestroy();
    }

    private void plantTree() {
        Timber.plant(new Timber.DebugTree());
    }

    private void setupDriver() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        if (hasBluetooth() && hasBluetoothLowEnergy()) {
            name("Thermopile");
            setDeviceClass(
                BluetoothClass.Service.INFORMATION,
                BluetoothClass.Device.COMPUTER_SERVER,
                BluetoothConfigManager.IO_CAPABILITY_IO
            );
            setProfiles(Arrays.asList(BluetoothProfile.GATT, BluetoothProfile.GATT_SERVER));
        }
    }

    private void setupMessenger() {
        incomingHandler = new IncomingHandler(this);
        serviceMessenger = new Messenger(incomingHandler);
    }

    private void destroyDriver() {
        bluetoothAdapter = null;
        bluetoothManager = null;
    }

    /**
     * Return true if Bluetooth is available.
     *
     * @return true if bluetoothAdapter is not null or it's address is empty,
     * otherwise Bluetooth is not supported on this hardware
     */
    @SuppressLint("HardwareIds")
    public boolean hasBluetooth() {
        final boolean result = bluetoothAdapter != null && !TextUtils.isEmpty(bluetoothAdapter.getAddress());
        incomingHandler.hasBluetooth = result;
        return result;
    }

    /**
     * Return true if Bluetooth LE is available.
     *
     * @return true if bluetoothAdapter is not null or it's address is empty and system has feature,
     * otherwise Bluetooth LE is not supported on this hardware
     */
    public boolean hasBluetoothLowEnergy() {
        final boolean result = hasBluetooth() && getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        incomingHandler.hasBluetoothLE = result;
        return result;
    }

    /**
     * Return true if Bluetooth is currently enabled and ready for use.
     *
     * @return true if the local adapter is turned on
     */
    public boolean isEnabled() {
        final boolean result = bluetoothAdapter.isEnabled();
        incomingHandler.isEnabled = result;
        return result;
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

    public void name(@NonNull final String name) {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.setName(name);
        }
    }

    public void setDeviceClass(final int service, final int device, final int ioCapability) {
        final BluetoothConfigManager configManager = BluetoothConfigManager.getInstance();
        configManager.setIoCapability(ioCapability);

        final BluetoothClass deviceClass = BluetoothClassFactory.build(service, device);

        configManager.setBluetoothClass(deviceClass);
    }

    public void setProfiles(@NonNull final List<Integer> profiles) {
        if (!profiles.contains(BluetoothProfile.GATT)) {
            profiles.add(BluetoothProfile.GATT);
        }

        final BluetoothProfileManager profileManager = BluetoothProfileManager.getInstance();
        profileManager.setEnabledProfiles(profiles);
    }

    /**
     * Initialize GATT server instance with callback.
     */
    public void startGattServer(@NonNull final BluetoothGattServerCallback bluetoothGattServerCallback) {
        this.gattServer = bluetoothManager.openGattServer(this, bluetoothGattServerCallback);
        this.isGattServerRunning = gattServer != null;
        incomingHandler.isGattServerRunning = gattServer != null;
    }

    /**
     * Shut down the GATT server.
     */
    public void stopGattServer() {
        if (gattServer != null && isGattServerRunning) {
            gattServer.close();
            gattServer = null;
            isGattServerRunning = false;
            incomingHandler.isGattServerRunning = false;
        }
    }

    public boolean isGattServerRunning() {
        return isGattServerRunning;
    }

    /**
     * Begin advertising over Bluetooth that this device is connectable and supports the selected service.
     */
    public void startAdvertising(
        @NonNull final AdvertiseSettings settings,
        @NonNull final AdvertiseData data,
        @NonNull final AdvertiseCallback callback) {
        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (bluetoothLeAdvertiser != null) {
            bluetoothLeAdvertiser.startAdvertising(settings, data, callback);
            this.advertiseCallback = callback;
            isAdvertising = true;
            incomingHandler.isAdvertising = true;
        }
    }

    /**
     * Stop Bluetooth advertisements.
     */
    public void stopAdvertising() {
        if (bluetoothLeAdvertiser != null) {
            bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
            advertiseCallback = null;
            isAdvertising = false;
            incomingHandler.isAdvertising = false;
        }
    }

    public boolean isAdvertising() {
        return isAdvertising;
    }

    //    /**
    //     * This will issue a request to make the local device discoverable to other devices. By default,
    //     * the device will become discoverable for 120 seconds.
    //     *
    //     * @param activity    Activity
    //     * @param requestCode request code
    //     */
    //    public void enableDiscoverability(@NonNull final Activity activity, final int requestCode) {
    //        enableDiscoverability(activity, requestCode, -1);
    //    }
    //
    //    /**
    //     * This will issue a request to make the local device discoverable to other devices. By default,
    //     * the device will become discoverable for 120 seconds.  Maximum duration is capped at 300
    //     * seconds.
    //     *
    //     * @param activity    Activity
    //     * @param requestCode request code
    //     * @param duration    discoverability duration in seconds
    //     */
    //    public void enableDiscoverability(@NonNull final Activity activity, final int requestCode, final int duration) {
    //        final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
    //        if (duration >= 0) {
    //            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
    //                duration > MAX_DISCOVERABILITY_PERIOD_SECONDS ? MAX_DISCOVERABILITY_PERIOD_SECONDS : duration);
    //        }
    //        activity.startActivityForResult(intent, requestCode);
    //    }

    /**
     * Observe BluetoothState. Possible values are:
     * {@link BluetoothAdapter#STATE_OFF},
     * {@link BluetoothAdapter#STATE_TURNING_ON},
     * {@link BluetoothAdapter#STATE_ON},
     * {@link BluetoothAdapter#STATE_TURNING_OFF},
     *
     * @return RxJava2 Observable getInstance BluetoothState
     */
    private void registerStateReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        stateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                bluetoothState = bluetoothAdapter.getState();
                incomingHandler.state = bluetoothState;

                final Bundle data = new Bundle();
                data.putInt(Keys.BLUETOOTH_STATE, bluetoothState);
                MessageFactory.sendToForeground(foregroundMessenger, MessageFactory.bluetooth(data));
            }
        };

        registerReceiver(stateReceiver, filter);
    }

    private void unregisterStateReceiver() {
        if (stateReceiver != null) {
            unregisterReceiver(stateReceiver);
            stateReceiver = null;
        }
    }

    public static class IncomingHandler extends Handler {


        public boolean hasBluetooth = false;
        public boolean hasBluetoothLE = false;
        public boolean isEnabled = false;
        public boolean isGattServerRunning = false;
        public boolean isAdvertising = false;
        public int state = BluetoothState.OFF;

        private final BluetoothService bluetoothService;

        final Bundle data = new Bundle();

        public IncomingHandler(@NonNull final BluetoothService bluetoothService) {
            this.bluetoothService = bluetoothService;
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case Action.REGISTER:
                    foregroundMessenger = message.replyTo;

                    data.clear();
                    data.putBoolean(Keys.HAS_BLUETOOTH, hasBluetooth);
                    data.putBoolean(Keys.HAS_BLUETOOTH_LE, hasBluetoothLE);
                    data.putBoolean(Keys.IS_BLUETOOTH_ENABLED, isEnabled);
                    data.putBoolean(Keys.IS_GATT_RUNNING, isGattServerRunning);
                    data.putBoolean(Keys.IS_ADVERTISING, isAdvertising);
                    data.putInt(Keys.BLUETOOTH_STATE, state);

                    MessageFactory.sendToForeground(message.replyTo, MessageFactory.bluetooth(data));
                    break;
                case Action.BLUETOOH_ENABLE:
                    isEnabled = bluetoothService.enable();
                    data.clear();
                    data.putBoolean(Keys.IS_BLUETOOTH_ENABLED, isEnabled);

                    MessageFactory.sendToForeground(message.replyTo, MessageFactory.bluetooth(data));
                    break;
                case Action.BLUETOOH_DISABLE:
                    isEnabled = !(bluetoothService.disable());
                    data.clear();
                    data.putBoolean(Keys.IS_BLUETOOTH_ENABLED, isEnabled);

                    MessageFactory.sendToForeground(message.replyTo, MessageFactory.bluetooth(data));
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }
}
