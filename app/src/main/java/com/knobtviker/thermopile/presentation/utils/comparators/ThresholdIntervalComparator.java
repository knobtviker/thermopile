package com.knobtviker.thermopile.presentation.utils.comparators;

import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;

import java.util.Comparator;

public class ThresholdIntervalComparator implements Comparator<ThresholdInterval> {

    public static ThresholdIntervalComparator create() {
        return new ThresholdIntervalComparator();
    }

    private ThresholdIntervalComparator() {
    }

    @Override
    public int compare(ThresholdInterval thresholdInterval, ThresholdInterval other) {
        if (thresholdInterval.equals(other)) {
            return 0;
        } else {
            return thresholdInterval.interval().isBefore(other.interval()) ? -1 : 1;
        }
    }
}
