package com.knobtviker.thermopile.data.models.presentation;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

/**
 * Created by bojan on 19/07/2017.
 */

@AutoValue
public abstract class Mode {

    public abstract long id();

    public abstract String name();

    public abstract Threshold threshold();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder id(final long value);

        public abstract Builder name(@NonNull final String value);

        public abstract Builder threshold(@NonNull final Threshold threshold);

        public abstract Mode build();
    }

    public static Builder builder() {
        return new AutoValue_Mode.Builder();
    }
}
