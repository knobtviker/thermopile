package com.knobtviker.thermopile.data.models.presentation;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

/**
 * Created by bojan on 15/06/2017.
 */

@AutoValue
public abstract class Settings {

    public abstract long id();

    public abstract String timezone();

    public abstract int formatClock();

    public abstract int formatDate();

    public abstract int formatTime();

    public abstract int unitTemperature();

    public abstract int unitPressure();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder id(final long value);

        public abstract Builder timezone(@NonNull final String value);

        public abstract Builder formatClock(final int value);

        public abstract Builder formatDate(final int value);

        public abstract Builder formatTime(final int value);

        public abstract Builder unitTemperature(final int value);

        public abstract Builder unitPressure(final int value);

        public abstract Settings build();
    }

    public static Builder builder() {
        return new AutoValue_Settings.Builder();
    }

    public static Settings EMPTY() {
        return builder()
            .id(-1L)
            .timezone("")
            .formatClock(-1)
            .formatDate(-1)
            .formatTime(-1)
            .unitTemperature(-1)
            .unitPressure(-1)
            .build();
    }
}
