package com.knobtviker.thermopile.data.sources.raw;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.knobtviker.thermopile.data.sources.RelayDataSource;

import java.io.IOException;
import java.util.Optional;

import io.reactivex.Completable;

/**
 * Created by bojan on 29/08/2017.
 */

public class RelayRawDataSource implements RelayDataSource.Raw {
    private static final String TAG = RelayRawDataSource.class.getSimpleName();

    private static Optional<RelayRawDataSource> INSTANCE = Optional.empty();

    private static final String GPIO_PIN = "BCM23";

    private final PeripheralManagerService peripheralManagerService;

    private final Gpio gpio;

    public static RelayRawDataSource getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new RelayRawDataSource());
        }
        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            INSTANCE = Optional.empty();
        }
    }

    private RelayRawDataSource() {
        peripheralManagerService = new PeripheralManagerService();

        gpio = create(GPIO_PIN, Gpio.DIRECTION_OUT_INITIALLY_LOW, Gpio.ACTIVE_HIGH);
    }

    public Gpio create(@NonNull final String pin, final int direction, final int activeType) {
        try {
            final Gpio gpio = peripheralManagerService.openGpio(pin);
            gpio.setDirection(direction);
            gpio.setActiveType(activeType);
            return gpio;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);

            return null;
        }
    }

    public Completable on() {
        return Completable.defer(() ->
            Completable.create(emitter -> {
                if (gpio == null) {
                    emitter.onError(new Throwable("GPIO --- " + GPIO_PIN + " --- is null"));
                } else {
                    if (!gpio.getValue()) {
                        gpio.setValue(true);
                    }
                    emitter.onComplete();
                }
            })
        );
    }

    public Completable off() {
        return Completable.defer(() ->
            Completable.create(emitter -> {
                if (gpio == null) {
                    emitter.onError(new Throwable("GPIO --- " + GPIO_PIN + " --- is null"));
                } else {
                    if (gpio.getValue()) {
                        gpio.setValue(false);
                    }
                    emitter.onComplete();
                }
            })
        );
    }
}
