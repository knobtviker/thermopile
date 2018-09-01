package com.knobtviker.thermopile.presentation.utils.mappers;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.models.presentation.Interval;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;

import org.threeten.bp.ZoneOffset;

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
            DateTimeKit.from(threshold.day, threshold.startHour, threshold.startMinute).toInstant(ZoneOffset.UTC),
            DateTimeKit.from(threshold.day, threshold.endHour, threshold.endMinute).toInstant(ZoneOffset.UTC).minusNanos(1)
        );
    }
}
