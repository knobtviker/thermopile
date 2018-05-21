package com.knobtviker.thermopile.presentation.utils.comparators;

import com.knobtviker.thermopile.data.models.local.Threshold;

import org.joda.time.DateTime;

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
        final DateTime dateTime1 = new DateTime()
            .withDayOfWeek(threshold.day + 1) //must be in range of 1 to 7
            .withHourOfDay(threshold.startHour)
            .withMinuteOfHour(threshold.startMinute)
            .withSecondOfMinute(0)
            .withMillisOfSecond(0);

        final DateTime dateTime2 = new DateTime()
            .withDayOfWeek(other.day + 1) //must be in range of 1 to 7
            .withHourOfDay(other.startHour)
            .withMinuteOfHour(other.startMinute)
            .withSecondOfMinute(0)
            .withMillisOfSecond(0);

        return dateTime1.equals(dateTime2) ? 0 : dateTime1.isBefore(dateTime2) ? -1 : 1;
    }
}
