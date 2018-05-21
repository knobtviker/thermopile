package com.knobtviker.thermopile.presentation.utils.mappers;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.function.Function;

public class ThresholdIntervalMapper implements Function<Threshold, ThresholdInterval> {

    public static ThresholdIntervalMapper create() {
        return new ThresholdIntervalMapper();
    }

    private ThresholdIntervalMapper() {
    }

    @Override
    public ThresholdInterval apply(Threshold threshold) {
        return ThresholdInterval.builder()
            .threshold(threshold)
            .interval(convertThresholdToInterval(threshold))
            .build();
    }

    private Interval convertThresholdToInterval(@NonNull final Threshold threshold) {
        final DateTime start = new DateTime()
            .withDayOfWeek(threshold.day + 1) //must be in range of 1 to 7
            .withHourOfDay(threshold.startHour)
            .withMinuteOfHour(threshold.startMinute)
            .withSecondOfMinute(0)
            .withMillisOfSecond(0);

        //this works for everyhing but 00:00 end time. which should not be allowed
        final DateTime end = new DateTime()
            .withDayOfWeek(threshold.day + 1)
            .withHourOfDay(threshold.endHour)
            .withMinuteOfHour(threshold.endMinute)
            .withSecondOfMinute(0)
            .withMillisOfSecond(0)
            .minusMillis(1);

        return new Interval(start, end);
    }
}
