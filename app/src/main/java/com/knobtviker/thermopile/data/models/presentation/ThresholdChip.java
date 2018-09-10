package com.knobtviker.thermopile.data.models.presentation;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ThresholdChip {

    public abstract long id();

    public abstract int day();

    public abstract int width();

    public abstract int offset();

    public abstract String temperature();

    public abstract String color();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder id(final long value);

        public abstract Builder day(final int value);

        public abstract Builder width(final int value);

        public abstract Builder offset(final int value);

        public abstract Builder temperature(@NonNull final String value);

        public abstract Builder color(@NonNull final String value);

        public abstract ThresholdChip build();
    }

    public static Builder builder() {
        return new AutoValue_ThresholdChip.Builder();
    }
}
