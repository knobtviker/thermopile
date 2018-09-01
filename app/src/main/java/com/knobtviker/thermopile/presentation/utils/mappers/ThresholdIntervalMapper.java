package com.knobtviker.thermopile.presentation.utils.mappers;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.models.presentation.Interval;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;

import org.threeten.bp.LocalTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.temporal.ChronoField;

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
        //this works for everyhing but 00:00 end time. which should not be allowed
        return Interval.of(
            ZonedDateTime.from(
                LocalTime.of(threshold.startHour, threshold.startMinute, 0, 0))
                .with(ChronoField.DAY_OF_WEEK, threshold.day + 1)
                .toInstant(),
            ZonedDateTime.from(
                LocalTime.of(threshold.endHour, threshold.endMinute, 0, 0))
                .with(ChronoField.DAY_OF_WEEK, threshold.day + 1)
                .minusNanos(1)
                .toInstant()
        );
    }
}
