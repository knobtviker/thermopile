package com.knobtviker.thermopile.data.sources.raw;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.view.Display;

import com.google.android.things.device.ScreenManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;

/**
 * Created by bojan on 18/12/2017.
 */

public class RxScreenManager {

    private static volatile RxScreenManager INSTANCE = null;

    private ScreenManager screenManager = null;

    public static RxScreenManager create(final int displayId) {
        if (INSTANCE == null) {
            synchronized (RxScreenManager.class) {
                if (INSTANCE == null) {
                    if (displayId == Display.INVALID_DISPLAY) {
                        throw new IllegalArgumentException("Display ID is invalid");
                    }
                    INSTANCE = new Builder(displayId).build();
                }
            }
        }

        return INSTANCE;
    }

    public static class Builder {
        private final int displayId;

        /**
         * Start building a new instance.
         */
        public Builder(final int displayId) {
            if (displayId == Display.INVALID_DISPLAY) {
                throw new IllegalArgumentException("Display ID cannot be invalid");
            }
            this.displayId = displayId;
        }

        /**
         * Create the instance.
         */
        public RxScreenManager build() {
            return new RxScreenManager(this.displayId);
        }
    }

    private RxScreenManager(final int displayId) {
        screenManager = new ScreenManager(displayId);
    }

    /**
     * Rotation.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ROTATION_CURRENT, ROTATION_0, ROTATION_90, ROTATION_180, ROTATION_270})
    public @interface Rotation {
    }

    public static final int ROTATION_CURRENT = ScreenManager.ROTATION_CURRENT;
    public static final int ROTATION_0 = ScreenManager.ROTATION_0;
    public static final int ROTATION_90 = ScreenManager.ROTATION_90;
    public static final int ROTATION_180 = ScreenManager.ROTATION_180;
    public static final int ROTATION_270 = ScreenManager.ROTATION_270;

    /**
     * Brightness range.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntRange(from = 0, to = 255)
    public @interface Brightness {
    }

    /**
     * Brightness mode.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MANUAL, AUTOMATIC})
    public @interface BrightnessMode {
    }

    public static final int MANUAL = ScreenManager.BRIGHTNESS_MODE_MANUAL;
    public static final int AUTOMATIC = ScreenManager.BRIGHTNESS_MODE_AUTOMATIC;

    /**
     * Locks the rotation of the screen to the specified rotation.
     */
    public Completable lockRotation(@Rotation final int rotation) {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    screenManager.lockRotation(rotation);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Unlocks the rotation of the screen.
     */
    public Completable unlockRotation() {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    screenManager.unlockRotation();
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Sets the screen backlight brightness.
     */
    public Completable brightness(@Brightness final int brightness) {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    screenManager.setBrightness(brightness);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Sets the brightness mode. Automatic mode requires a TYPE_LIGHT sensor outputing values in Lux.
     */
    public Completable brightnessMode(@BrightnessMode final int brightnessMode) {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    screenManager.setBrightnessMode(brightnessMode);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Sets the display density of the screen.
     */
    public Completable displayDensity(final int density) {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    screenManager.setDisplayDensity(density);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Sets the scale by which font sizes are multiplied.
     */
    public Completable fontScale(final float fontScale) {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    screenManager.setFontScale(fontScale);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Sets the duration of inactivity before the device goes to sleep.
     */
    public Completable screenOffTimeout(final long timeoutInMilliseconds) {
        return Completable.create(emitter -> {
            if (!emitter.isDisposed()) {
                try {
                    screenManager.setScreenOffTimeout(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }
}
