package com.knobtviker.thermopile.data.sources.raw;

import android.os.LocaleList;
import android.support.annotation.NonNull;

import com.google.android.things.AndroidThings;
import com.google.android.things.device.DeviceManager;

import io.reactivex.Completable;
import io.reactivex.Observable;

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
        deviceManager = new DeviceManager();
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

    /**
     * Gets the major version number of AndroidThings library linked at runtime to the app.
     */
    public Observable<Integer> versionMajor() {
        return Observable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    emitter.onNext(AndroidThings.getVersionMajor());
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Gets the minor version number of AndroidThings library linked at runtime to the app.
     */
    public Observable<Integer> versionMinor() {
        return Observable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    emitter.onNext(AndroidThings.getVersionMinor());
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Gets the revision version number of AndroidThings library linked at runtime to the app.
     */
    public Observable<Integer> versionRevision() {
        return Observable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    emitter.onNext(AndroidThings.getVersionRevision());
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Gets the version tag of AndroidThings library linked at runtime to the app.
     */
    public Observable<String> versionTag() {
        return Observable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    emitter.onNext(AndroidThings.getVersionTag());
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Gets the complete version of AndroidThings library linked at runtime to the app as string.
     */
    public Observable<String> version() {
        return Observable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    emitter.onNext(AndroidThings.getVersionString());
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }
}
