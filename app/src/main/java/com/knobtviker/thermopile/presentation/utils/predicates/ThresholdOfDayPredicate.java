package com.knobtviker.thermopile.presentation.utils.predicates;

import com.knobtviker.thermopile.data.models.local.Threshold;

import java.util.function.Predicate;

public class ThresholdOfDayPredicate implements Predicate<Threshold> {
    private final int day;

    public static ThresholdOfDayPredicate create(final int day) {
        return new ThresholdOfDayPredicate(day);
    }

    private ThresholdOfDayPredicate(int day) {
        this.day = day;
    }

    @Override
    public boolean test(Threshold threshold) {
        return threshold.day == day;
    }
}
