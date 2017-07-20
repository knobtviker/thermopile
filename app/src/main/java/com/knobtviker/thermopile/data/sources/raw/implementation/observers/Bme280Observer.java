package com.knobtviker.thermopile.data.sources.raw.implementation.observers;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.raw.Triplet;
import com.knobtviker.thermopile.data.sources.raw.implementation.drivers.Bme280;

import java.io.IOException;

import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;

/**
 * Created by bojan on 19/07/2017.
 */

public class Bme280Observer implements SingleSource<Triplet<Float, Float, Float>> {

    private final String bus;

    public static Bme280Observer create(@NonNull final String bus) {
        return new Bme280Observer(bus);
    }

    private Bme280Observer(@NonNull final String bus) {
        this.bus = bus;
    }

    @SuppressWarnings("UnnecessaryBoxing")
    @Override
    public void subscribe(SingleObserver<? super Triplet<Float, Float, Float>> observer) {
        try {
            final Bme280 bme280 = new Bme280(bus);
            bme280.setTemperatureOversampling(Bme280.OVERSAMPLING_1X);
            bme280.setHumidityOversampling(Bme280.OVERSAMPLING_1X);
            bme280.setPressureOversampling(Bme280.OVERSAMPLING_1X);
            bme280.setMode(Bme280.MODE_NORMAL);

            final float[] readings = bme280.readAll();
            observer.onSuccess(
                Triplet.<Float, Float, Float>builder()
                    .first(Float.valueOf(readings[0]))
                    .second(Float.valueOf(readings[1]))
                    .third(Float.valueOf(readings[2]))
                    .build()
            );
            bme280.close();
        } catch (IOException e) {
            observer.onError(e);
        }
    }
}
