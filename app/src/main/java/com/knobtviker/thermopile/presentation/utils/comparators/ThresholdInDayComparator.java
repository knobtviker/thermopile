package com.knobtviker.thermopile.presentation.utils.comparators;

import com.knobtviker.thermopile.data.models.local.Threshold;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.temporal.ChronoField;

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
        final LocalDateTime dateTime1 = LocalDateTime.from(
            LocalTime.of(threshold.startHour, threshold.startMinute, 0,0))
            .with(ChronoField.DAY_OF_WEEK, threshold.day + 1);

        final LocalDateTime dateTime2 = LocalDateTime.from(
            LocalTime.of(other.startHour, other.startMinute, 0,0))
            .with(ChronoField.DAY_OF_WEEK, other.day + 1);

        return dateTime1.equals(dateTime2) ? 0 : dateTime1.isBefore(dateTime2) ? -1 : 1;
    }
}
