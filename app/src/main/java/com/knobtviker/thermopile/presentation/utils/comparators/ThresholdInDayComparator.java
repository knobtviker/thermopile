package com.knobtviker.thermopile.presentation.utils.comparators;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;

import java.util.Comparator;

public class ThresholdInDayComparator implements Comparator<Threshold> {

    public static ThresholdInDayComparator create() {
        return new ThresholdInDayComparator();
    }

    private ThresholdInDayComparator() {
    }

    //Sort found thresholds per start time hour and minute
    @Override
    public int compare(Threshold threshold, Threshold other) {
        return DateTimeKit.from(threshold.day,threshold.startHour, threshold.startMinute)
            .equals(DateTimeKit.from(other.day,other.startHour, other.startMinute)) ?
            0 : DateTimeKit.from(threshold.day,threshold.startHour, threshold.startMinute)
            .isBefore(DateTimeKit.from(other.day,other.startHour, other.startMinute)) ? -1 : 1;
    }
}
