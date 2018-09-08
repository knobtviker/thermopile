package com.knobtviker.thermopile.data.models.presentation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class MeasuredData {

    public abstract float value();

    public abstract float progress();

    @Nullable
    public abstract Integer color();

    public abstract String readableValue();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder value(final float value);

        public abstract Builder progress(final float value);

        public abstract Builder color(@NonNull final Integer value);

        public abstract Builder readableValue(@NonNull final String value);

        public abstract MeasuredData build();
    }

    public static Builder builder() {
        return new AutoValue_MeasuredData.Builder();
    }
}
