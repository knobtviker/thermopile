package com.knobtviker.thermopile.data.sources.raw.bme680;

import static com.knobtviker.thermopile.data.sources.raw.bme680.Bme680.FILTER_SIZE_NONE;
import static com.knobtviker.thermopile.data.sources.raw.bme680.Bme680.Filter;
import static com.knobtviker.thermopile.data.sources.raw.bme680.Bme680.OVERSAMPLING_SKIPPED;
import static com.knobtviker.thermopile.data.sources.raw.bme680.Bme680.Oversampling;

/**
 * Created by bojan on 27/11/2017.
 */

public class SensorSettings {

    // Humidity oversampling
    @Oversampling
    public int oversamplingHumidity = OVERSAMPLING_SKIPPED;

    // Temperature oversampling
    @Oversampling
    public int oversamplingTemperature = OVERSAMPLING_SKIPPED;

    // Pressure oversampling
    @Oversampling
    public int oversamplingPressure = OVERSAMPLING_SKIPPED;

    // Filter coefficient
    @Filter
    public int filter = FILTER_SIZE_NONE;
}
