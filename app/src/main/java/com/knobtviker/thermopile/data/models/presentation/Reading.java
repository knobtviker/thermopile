package com.knobtviker.thermopile.data.models.presentation;

import com.google.auto.value.AutoValue;

/**
 * Created by bojan on 15/06/2017.
 */

@AutoValue
public abstract class Reading {

    public abstract long id();

    public abstract long timestamp();

    public abstract float temperature();

    public abstract float humidity();

    public abstract float pressure();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder id(final long value);

        public abstract Builder timestamp(final long value);

        public abstract Builder temperature(final float value);

        public abstract Builder humidity(final float value);

        public abstract Builder pressure(final float value);

        public abstract Reading build();
    }

    public static Builder builder() {
        return new AutoValue_Reading.Builder();
    }
}
