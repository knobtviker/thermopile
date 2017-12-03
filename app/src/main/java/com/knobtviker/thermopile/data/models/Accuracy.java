package com.knobtviker.thermopile.data.models;

import com.google.auto.value.AutoValue;

/**
 * Created by bojan on 03/12/2017.
 */

@AutoValue
public abstract class Accuracy {

    public abstract int temperature();

    public abstract int humidity();

    public abstract int pressure();

    public abstract int light();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder temperature(final int value);

        public abstract Builder humidity(final int value);

        public abstract Builder pressure(final int value);

        public abstract Builder light(final int value);

        public abstract Accuracy build();
    }

    public static Builder builder() {
        return new AutoValue_Accuracy.Builder();
    }

    abstract Builder toBuilder();

    public Accuracy withTemperature(final int value) {
        return toBuilder()
            .temperature(value)
            .build();
    }

    public Accuracy withHumidity(final int value) {
        return toBuilder()
            .humidity(value)
            .build();
    }

    public Accuracy withPressure(final int value) {
        return toBuilder()
            .pressure(value)
            .build();
    }

    public Accuracy withLight(final int value) {
        return toBuilder()
            .light(value)
            .build();
    }

    public static Accuracy EMPTY = AutoValue_Accuracy.builder()
        .temperature(-1)
        .humidity(-1)
        .pressure(-1)
        .light(-1)
        .build();
}
