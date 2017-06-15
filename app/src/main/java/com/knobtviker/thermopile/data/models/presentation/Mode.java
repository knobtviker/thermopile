package com.knobtviker.thermopile.data.models.presentation;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

/**
 * Created by bojan on 15/06/2017.
 */

@AutoValue
public abstract class Mode {

    public abstract long id();

    public abstract String name();

    public abstract int color();

    public abstract long lastModified();

    public abstract int startHour();

    public abstract int startMinute();

    public abstract int endHour();

    public abstract int endMinute();

    public abstract ImmutableList<Integer> days();

    public abstract float temperature();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder id(final long value);

        public abstract Builder name(@NonNull final String value);

        public abstract Builder color(@NonNull final int value);

        public abstract Builder lastModified(final long value);

        public abstract Builder startHour(final int value);

        public abstract Builder startMinute(final int value);

        public abstract Builder endHour(final int value);

        public abstract Builder endMinute(final int value);

        public abstract Builder days(@NonNull final ImmutableList<Integer> value);

        public abstract Builder temperature(final float value);

        public abstract Mode build();
    }

    public static Builder builder() {
        return new AutoValue_Mode.Builder();
    }
}
