package com.knobtviker.thermopile.data.sources.beta;

import android.os.LocaleList;
import android.support.annotation.NonNull;

import com.google.android.things.device.DeviceManager;

import io.reactivex.Completable;

/**
 * Created by bojan on 24/12/2017.
 */

public class RxDeviceManager {

    private static volatile RxDeviceManager INSTANCE = null;

    private DeviceManager deviceManager = null;

    public static RxDeviceManager create() {
        if (INSTANCE == null) {
            synchronized (RxDeviceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RxDeviceManager.Builder().build();
                }
            }
        }

        return INSTANCE;
    }

    public static class Builder {

        /**
         * Start building a new instance.
         */
        public Builder() {
        }

        /**
         * Create the instance.
         */
        public RxDeviceManager build() {
            return new RxDeviceManager();
        }
    }

    private RxDeviceManager() {
        deviceManager = DeviceManager.getInstance();
    }

    /**
     * Perform device factory reset.
     */
    public Completable factoryReset(final boolean wipeExternalStorage) {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    deviceManager.factoryReset(wipeExternalStorage);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Perform device reboot with default parameters.
     */
    public Completable reboot() {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    deviceManager.reboot();
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Set the list of preferred system locales.
     * LocaleList: the list of preferred system locales.
     * The order the locale appears in this list determines its priority for being used.
     */
    public Completable setSystemLocales(@NonNull final LocaleList localeList) {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    if (localeList.isEmpty()) {
                        emitter.onError(new Throwable("LocaleList cannot be empty"));
                    } else {
                        deviceManager.setSystemLocales(localeList);
                        emitter.onComplete();
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }
}
