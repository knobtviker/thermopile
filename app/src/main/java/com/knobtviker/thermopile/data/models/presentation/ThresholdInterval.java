package com.knobtviker.thermopile.data.models.presentation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.knobtviker.thermopile.data.models.local.Threshold;

import org.joda.time.Interval;

@AutoValue
public abstract class ThresholdInterval {

    @Nullable
    public abstract Threshold threshold();

    public abstract Interval interval();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder threshold(@Nullable final Threshold value);

        public abstract Builder interval(@NonNull final Interval value);

        public abstract ThresholdInterval build();
    }

    public static Builder builder() {
        return new AutoValue_ThresholdInterval.Builder();
    }
}
