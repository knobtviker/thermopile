package com.knobtviker.thermopile.data.models.presentation;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Atmosphere {

    public abstract long timestamp();

    public abstract float temperature();

    public abstract float humidity();

    public abstract float pressure();

    public abstract float airQuality();

    public abstract float altitude();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder timestamp(final long value);

        public abstract Builder temperature(final float value);

        public abstract Builder humidity(final float value);

        public abstract Builder pressure(final float value);

        public abstract Builder airQuality(final float value);

        public abstract Builder altitude(final float value);

        public abstract Atmosphere build();
    }

    public static Builder builder() {
        return new AutoValue_Atmosphere.Builder();
    }

    abstract Builder toBuilder();

    public static Atmosphere EMPTY() {
        return AutoValue_Atmosphere.builder()
            .timestamp(0L)
            .temperature(0.0f)
            .pressure(0.0f)
            .humidity(0.0f)
            .airQuality(0.0f)
            .altitude(0.0f)
            .build();
    }

    public Atmosphere withTimestamp(final long value) {
        return toBuilder()
            .timestamp(value)
            .build();
    }

    public Atmosphere withTemperature(final float value) {
        return toBuilder()
            .temperature(value)
            .build();
    }

    public Atmosphere withHumidity(final float value) {
        return toBuilder()
            .humidity(value)
            .build();
    }

    public Atmosphere withPressure(final float value) {
        return toBuilder()
            .pressure(value)
            .build();
    }

    public Atmosphere withAirQuality(final float value) {
        return toBuilder()
            .airQuality(value)
            .build();
    }

    public Atmosphere withAltitude(final float value) {
        return toBuilder()
            .altitude(value)
            .build();
    }
}
