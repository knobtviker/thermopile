package com.knobtviker.thermopile.fram;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.knobtviker.android.things.contrib.community.boards.BoardDefaults;
import com.knobtviker.android.things.contrib.community.driver.fram.Mb85rc256v;
import com.knobtviker.thermopile.shared.MessageFactory;
import com.knobtviker.thermopile.shared.constants.Keys;
import com.knobtviker.thermopile.shared.message.Action;

import java.io.IOException;
import java.nio.ByteBuffer;

import timber.log.Timber;

public class Mb85rc256vService extends Service {

    public final static int ADDRESS_LAST_BOOT_TIMESTAMP = 0;
    public final static int ADDRESS_BOOT_COUNT = Long.BYTES; //1 * Long.BYTES to shift for 8 bytes, next address is 2 * Long.BYTES

    @NonNull
    private Messenger serviceMessenger;

    @Nullable
    private Mb85rc256v fram;

    @Override
    public void onCreate() {
        super.onCreate();
        plantTree();
        setupDriver();
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
        destroyDriver();
        super.onDestroy();
    }

    private void plantTree() {
        Timber.plant(new Timber.DebugTree());
    }

    private void setupDriver() {
        try {
            fram = new Mb85rc256v(BoardDefaults.defaultI2CBus());
            fram.setVerbose(BuildConfig.DEBUG);
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    private void setupMessenger() {
        serviceMessenger = new Messenger(new IncomingHandler(fram));
    }

    private void destroyDriver() {
        if (fram != null) {
            try {
                fram.close();
                fram = null;
            } catch (IOException e) {
                Timber.e(e);
            }
        }
    }

    public static class IncomingHandler extends Handler {

        @Nullable
        private Mb85rc256v fram;

        private long lastBootTimestamp = 0L;

        private long bootCount = 0L;

        IncomingHandler(@Nullable final Mb85rc256v fram) {
            this.fram = fram;

            try {
                if (fram != null) {
                    final long timestamp = bytesToLong(fram.readArray(ADDRESS_LAST_BOOT_TIMESTAMP, Long.BYTES));
                    final long now = SystemClock.currentThreadTimeMillis();

                    if (timestamp <= now) {
                        lastBootTimestamp = now;
                    } else {
                        lastBootTimestamp = timestamp;
                    }

                    final long count = bytesToLong(fram.readArray(ADDRESS_BOOT_COUNT, Long.BYTES));
                    if (count == 0L) {
                        bootCount = 1L;
                    } else {
                        bootCount = count;
                    }

                    fram.writeArray(ADDRESS_LAST_BOOT_TIMESTAMP, longToBytes(lastBootTimestamp));
                    fram.writeArray(ADDRESS_BOOT_COUNT, longToBytes(bootCount + 1L));
                } else {
                    lastBootTimestamp = SystemClock.currentThreadTimeMillis();
                    bootCount = 1L;
                }
            } catch (IOException e) {
                lastBootTimestamp = SystemClock.currentThreadTimeMillis();
                bootCount = 1L;
                Timber.e(e);
            }
        }

        @Override
        public void handleMessage(Message message) {
            final Bundle data = new Bundle();

            switch (message.what) {
                case Action.REGISTER:
                    data.putLong(Keys.LAST_BOOT_TIMESTAMP, lastBootTimestamp);
                    data.putLong(Keys.BOOT_COUNT, bootCount);

                    MessageFactory.sendToForeground(message.replyTo, MessageFactory.fram(data));
                    break;
                case Action.RESET:
                    reset();

                    data.putLong(Keys.LAST_BOOT_TIMESTAMP, lastBootTimestamp);
                    data.putLong(Keys.BOOT_COUNT, bootCount);

                    MessageFactory.sendToForeground(message.replyTo, MessageFactory.fram(data));
                    break;
                case Action.LAST_BOOT_TIMESTAMP:
                    data.putLong(Keys.LAST_BOOT_TIMESTAMP, lastBootTimestamp);

                    MessageFactory.sendToForeground(message.replyTo, MessageFactory.fram(data));
                case Action.BOOT_COUNT:
                    data.putLong(Keys.BOOT_COUNT, bootCount);

                    MessageFactory.sendToForeground(message.replyTo, MessageFactory.fram(data));
                default:
                    super.handleMessage(message);
            }
        }

        private void reset() {
            bootCount = 1L;
            lastBootTimestamp = SystemClock.currentThreadTimeMillis();

            if (fram != null) {
                try {
                    fram.writeArray(ADDRESS_BOOT_COUNT, longToBytes(bootCount + 1L));
                    fram.writeArray(ADDRESS_LAST_BOOT_TIMESTAMP, longToBytes(lastBootTimestamp));
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
        }

        private byte[] longToBytes(final long x) {
            final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.putLong(x);
            return buffer.array();
        }

        private long bytesToLong(final byte[] bytes) {
            final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.put(bytes);
            buffer.flip(); //need flip
            return buffer.getLong();
        }
    }
}
