package com.knobtviker.thermopile.data.sources.raw;

import com.knobtviker.thermopile.data.models.raw.Triplet;
import com.knobtviker.thermopile.data.sources.ReadingDataSource;
import com.knobtviker.thermopile.data.sources.raw.implementation.observers.Bme280Observer;

import java.util.Optional;

import io.reactivex.Single;

/**
 * Created by bojan on 26/06/2017.
 */

public class ReadingRawDataSource implements ReadingDataSource.Raw {

    private static Optional<ReadingRawDataSource> INSTANCE = Optional.empty();

    private static final String BUS_I2C = "I2C1";

    public static ReadingRawDataSource getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new ReadingRawDataSource());
        }
        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            INSTANCE = Optional.empty();
        }
    }

    private ReadingRawDataSource() {
    }

    @Override
    public Single<Triplet<Float, Float, Float>> read() {
        return Single.defer(() -> Bme280Observer.create(BUS_I2C));
    }
}
