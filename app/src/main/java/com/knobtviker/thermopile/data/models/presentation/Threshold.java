package com.knobtviker.thermopile.data.models.presentation;

import com.google.auto.value.AutoValue;

/**
 * Created by bojan on 15/06/2017.
 */

@AutoValue
public abstract class Threshold {

    public abstract int day();

    public abstract int startHour();

    public abstract int startMinute();

    public abstract int endHour();

    public abstract int endMinute();

    public abstract int temperature();

    public abstract int color();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder day(final int value);

        public abstract Builder startHour(final int value);

        public abstract Builder startMinute(final int value);

        public abstract Builder endHour(final int value);

        public abstract Builder endMinute(final int value);

        public abstract Builder temperature(final int value);

        public abstract Builder color(final int value);

        public abstract Threshold build();
    }

    public static Builder builder() {
        return new AutoValue_Threshold.Builder();
    }
}
