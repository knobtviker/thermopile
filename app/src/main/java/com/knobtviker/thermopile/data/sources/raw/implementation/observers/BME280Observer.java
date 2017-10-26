package com.knobtviker.thermopile.data.sources.raw.implementation.observers;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.raw.Triplet;
import com.knobtviker.thermopile.data.sources.raw.implementation.drivers.BME280;

import java.io.IOException;

import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;

/**
 * Created by bojan on 19/07/2017.
 */

public class BME280Observer implements SingleSource<Triplet<Float, Float, Float>> {

    private final String bus;

    public static BME280Observer create(@NonNull final String bus) {
        return new BME280Observer(bus);
    }

    private BME280Observer(@NonNull final String bus) {
        this.bus = bus;
    }

    @SuppressWarnings("UnnecessaryBoxing")
    @Override
    public void subscribe(SingleObserver<? super Triplet<Float, Float, Float>> observer) {
        try {
            final BME280 bme280 = new BME280(bus);
            bme280.setTemperatureOversampling(BME280.OVERSAMPLING_1X);
            bme280.setHumidityOversampling(BME280.OVERSAMPLING_1X);
            bme280.setPressureOversampling(BME280.OVERSAMPLING_1X);
            bme280.setMode(BME280.MODE_NORMAL);

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
