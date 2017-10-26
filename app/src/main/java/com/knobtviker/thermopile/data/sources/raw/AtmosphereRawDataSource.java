package com.knobtviker.thermopile.data.sources.raw;

import com.knobtviker.thermopile.data.models.raw.Triplet;
import com.knobtviker.thermopile.data.sources.AtmosphereDataSource;
import com.knobtviker.thermopile.data.sources.raw.implementation.observers.BME280Observer;
import com.knobtviker.thermopile.data.sources.raw.implementation.observers.TSL2561Observer;

import java.util.Optional;

import io.reactivex.Single;

/**
 * Created by bojan on 26/06/2017.
 */

public class AtmosphereRawDataSource implements AtmosphereDataSource.Raw {

    private static Optional<AtmosphereRawDataSource> INSTANCE = Optional.empty();

    private static final String BUS_I2C = "I2C1";

    public static AtmosphereRawDataSource getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new AtmosphereRawDataSource());
        }
        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            INSTANCE = Optional.empty();
        }
    }

    private AtmosphereRawDataSource() {
    }

    @SuppressWarnings("UnnecessaryBoxing")
    @Override
    public Single<Triplet<Float, Float, Float>> read() {
        return Single.defer(() -> BME280Observer.create(BUS_I2C));
    }

    @Override
    public Single<Float> readLuminosity() {
        return Single.defer(() -> TSL2561Observer.create(BUS_I2C));
    }
}
