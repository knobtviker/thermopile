package com.knobtviker.thermopile.data.models.presentation;

import com.google.auto.value.AutoValue;

/**
 * Created by bojan on 27/07/2017.
 */

@AutoValue
public abstract class Hour {

    public abstract int hour();

    public abstract int startMinutes();

    public abstract int endMinutes();

    public abstract int color();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder hour(final int value);

        public abstract Builder startMinutes(final int value);

        public abstract Builder endMinutes(final int value);

        public abstract Builder color(final int value);

        public abstract Hour build();
    }

    abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_Hour.Builder();
    }

    public Hour withColor(final int value) {
        return toBuilder()
            .color(value)
            .build();
    }

    public Hour withStartMinutes(final int value) {
        return toBuilder()
            .startMinutes(value)
            .build();
    }

    public Hour withEndMinutes(final int value) {
        return toBuilder()
            .endMinutes(value)
            .build();
    }
}
