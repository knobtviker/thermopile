package com.knobtviker.thermopile.data.sources.beta;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.google.android.things.device.TimeManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.reactivex.Completable;

/**
 * Created by bojan on 25/12/2017.
 */

public class RxTimeManager {

    private static volatile RxTimeManager INSTANCE = null;

    private TimeManager timeManager = null;

    public static RxTimeManager create() {
        if (INSTANCE == null) {
            synchronized (RxTimeManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RxTimeManager.Builder().build();
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
        public RxTimeManager build() {
            return new RxTimeManager();
        }
    }

    private RxTimeManager() {
        timeManager = TimeManager.getInstance();
    }

    /**
     * Time format.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FORMAT_12, FORMAT_24})
    public @interface Format {
    }

    public static final int FORMAT_12 = TimeManager.FORMAT_12;
    public static final int FORMAT_24 = TimeManager.FORMAT_24;

    /**
     * Sets whether or not wall clock time should sync with automatic time updates from NTP.
     */
    public Completable setAutoTimeEnabled(final boolean enabled) {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    timeManager.setAutoTimeEnabled(enabled);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Set the system wall clock time.
     * This method does not guarentee persistance past reboot.
     * This method also disables automatic time updates from NTP.
     */
    public Completable setTime(final long millis) {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    timeManager.setTime(millis);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Sets the time format for the device.
     */
    public Completable setTimeFormat(@Format final int format) {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    timeManager.setTimeFormat(format);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Set the system's persistent default time zone. This sets the time zone for all apps, even after reboot.
     * timezoneId is one of the Olson ids from the list returned by TimeZone.getAvailableIDs()
     */
    public Completable setTimeZone(@NonNull final String timezoneId) {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    timeManager.setTimeZone(timezoneId);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }
}
