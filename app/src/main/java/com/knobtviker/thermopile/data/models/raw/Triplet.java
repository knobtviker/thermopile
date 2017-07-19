package com.knobtviker.thermopile.data.models.raw;

import com.google.auto.value.AutoValue;

/**
 * Created by bojan on 19/07/2017.
 */

@AutoValue
public abstract class Triplet<F, S, T> {

    public abstract F first();

    public abstract S second();

    public abstract T third();

    public static <F, S, T> Triplet<F, S, T> create(F first, S second, T third) {
        return Triplet.<F, S, T>builder()
            .first(first)
            .second(second)
            .third(third)
            .build();
    }

    @AutoValue.Builder
    public abstract static class Builder<F, S, T> {

        public abstract Builder<F, S, T> first(final F value);

        public abstract Builder<F, S, T> second(final S value);

        public abstract Builder<F, S, T> third(final T value);

        public abstract Triplet<F, S, T> build();
    }

    public static <F, S, T> Builder<F, S, T> builder() {
        return new AutoValue_Triplet.Builder<>();
    }
}
