package com.knobtviker.thermopile.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.things.bluetooth.BluetoothClassFactory;
import com.google.android.things.bluetooth.BluetoothConfigManager;
import com.google.android.things.bluetooth.BluetoothProfileManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import timber.log.Timber;

public class BluetoothService extends Service {

    public final static int MAX_DISCOVERABILITY_PERIOD_SECONDS = 300;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
        State.OFF,
        State.TURNING_ON,
        State.ON,
        State.TURNING_OFF
    })

    public @interface State {

        int OFF = BluetoothAdapter.STATE_OFF;
        int TURNING_ON = BluetoothAdapter.STATE_TURNING_ON;
        int ON = BluetoothAdapter.STATE_ON;
        int TURNING_OFF = BluetoothAdapter.STATE_TURNING_OFF;
    }

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

    @State
    private int bluetoothState = State.OFF;

    private boolean isBluetoothRunning = false;
    private boolean isGattServerRunning = false;
    private boolean isAdvertising = false;

    @Override
    public void onCreate() {
        super.onCreate();
        plantTree();
        setupDriver();
        registerStateReceiver();
        setupMessenger();
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

    private void setupMessenger() {
        //        incomingHandler = new IncomingHandler(fram);
        serviceMessenger = new Messenger(incomingHandler);
    }

    private void setupDriver() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
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
        return bluetoothAdapter != null && !TextUtils.isEmpty(bluetoothAdapter.getAddress());
    }

    /**
     * Return true if Bluetooth LE is available.
     *
     * @return true if bluetoothAdapter is not null or it's address is empty and system has feature,
     * otherwise Bluetooth LE is not supported on this hardware
     */
    public boolean hasBluetoothLowEnergy() {
        return hasBluetooth() && getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
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

    public void name(@NonNull final String name) {
        if (bluetoothAdapter != null && isBluetoothRunning) {
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
    }

    /**
     * Shut down the GATT server.
     */
    public void stopGattServer() {
        if (gattServer != null && isGattServerRunning) {
            gattServer.close();
            gattServer = null;
            isGattServerRunning = false;
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
        }
    }

    public boolean isAdvertising() {
        return isAdvertising;
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
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration > MAX_DISCOVERABILITY_PERIOD_SECONDS ? MAX_DISCOVERABILITY_PERIOD_SECONDS : duration);
        }
        activity.startActivityForResult(discoverableIntent, requestCode);
    }

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

        //        @Nullable
        //        private Mb85rc256v fram;

        //        IncomingHandler(@Nullable final Mb85rc256v fram) {
        //            this.fram = fram;
        //
        //            try {
        //                if (fram != null) {
        //                    final long timestamp = bytesToLong(fram.readArray(ADDRESS_LAST_BOOT_TIMESTAMP, Long.BYTES));
        //                    final long now = SystemClock.currentThreadTimeMillis();
        //
        //                    if (timestamp <= now) {
        //                        lastBootTimestamp = now;
        //                    } else {
        //                        lastBootTimestamp = timestamp;
        //                    }
        //
        //                    final long count = bytesToLong(fram.readArray(ADDRESS_BOOT_COUNT, Long.BYTES));
        //                    if (count == 0L) {
        //                        bootCount = 1L;
        //                    } else {
        //                        bootCount = count;
        //                    }
        //
        //                    fram.writeArray(ADDRESS_LAST_BOOT_TIMESTAMP, longToBytes(lastBootTimestamp));
        //                    fram.writeArray(ADDRESS_BOOT_COUNT, longToBytes(bootCount + 1L));
        //                } else {
        //                    lastBootTimestamp = SystemClock.currentThreadTimeMillis();
        //                    bootCount = 1L;
        //                }
        //            } catch (IOException e) {
        //                lastBootTimestamp = SystemClock.currentThreadTimeMillis();
        //                bootCount = 1L;
        //                Timber.e(e);
        //            }
        //        }
        //
        //        @Override
        //        public void handleMessage(Message message) {
        //            switch (message.what) {
        //                case MessageWhatUser.REGISTER:
        //                    foregroundMessenger = message.replyTo;
        //
        //                    sendMessageToForeground(buildLongValueMessage(MessageWhatData.LAST_BOOT_TIMESTAMP, lastBootTimestamp));
        //                    sendMessageToForeground(buildLongValueMessage(MessageWhatData.BOOT_COUNT, bootCount));
        //                    break;
        //                case MessageWhatUser.RESET:
        //                    reset();
        //
        //                    sendMessageToForeground(buildLongValueMessage(MessageWhatData.LAST_BOOT_TIMESTAMP, lastBootTimestamp));
        //                    sendMessageToForeground(buildLongValueMessage(MessageWhatData.BOOT_COUNT, bootCount));
        //                    break;
        //                case MessageWhatData.LAST_BOOT_TIMESTAMP:
        //                    sendMessageToForeground(buildLongValueMessage(MessageWhatData.LAST_BOOT_TIMESTAMP, lastBootTimestamp));
        //                case MessageWhatData.BOOT_COUNT:
        //                    sendMessageToForeground(buildLongValueMessage(MessageWhatData.BOOT_COUNT, bootCount));
        //                default:
        //                    super.handleMessage(message);
        //            }
        //        }
        //
        //        private static Message buildLongValueMessage(@MessageWhatData final int messageWhat, final long normalizedValue) {
        //            final Message message = Message.obtain(null, messageWhat);
        //
        //            message.setData(buildLongValueBundle(normalizedValue));
        //
        //            return message;
        //        }
        //
        //        private static Bundle buildLongValueBundle(final long normalizedValue) {
        //            final Bundle bundle = new Bundle();
        //
        //            bundle.putLong("value", normalizedValue);
        //
        //            return bundle;
        //        }
        //
        //        private void sendMessageToForeground(@NonNull final Message message) {
        //            try {
        //                if (foregroundMessenger != null) {
        //                    foregroundMessenger.send(message);
        //                }
        //            } catch (RemoteException e) {
        //                Timber.e(e);
        //            }
        //        }
        //
        //        private void reset() {
        //            bootCount = 1L;
        //            lastBootTimestamp = SystemClock.currentThreadTimeMillis();
        //
        //            if (fram != null) {
        //                try {
        //                    fram.writeArray(ADDRESS_BOOT_COUNT, longToBytes(bootCount + 1L));
        //                    fram.writeArray(ADDRESS_LAST_BOOT_TIMESTAMP, longToBytes(lastBootTimestamp));
        //                } catch (IOException e) {
        //                    Timber.e(e);
        //                }
        //            }
        //        }
        //
        //        private byte[] longToBytes(final long x) {
        //            final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        //            buffer.putLong(x);
        //            return buffer.array();
        //        }
        //
        //        private long bytesToLong(final byte[] bytes) {
        //            final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        //            buffer.put(bytes);
        //            buffer.flip(); //need flip
        //            return buffer.getLong();
        //        }
    }
}
