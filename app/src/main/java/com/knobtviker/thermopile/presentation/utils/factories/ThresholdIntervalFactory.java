package com.knobtviker.thermopile.presentation.utils.factories;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.models.presentation.Interval;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;
import com.knobtviker.thermopile.presentation.utils.comparators.ThresholdInDayComparator;
import com.knobtviker.thermopile.presentation.utils.comparators.ThresholdIntervalComparator;
import com.knobtviker.thermopile.presentation.utils.mappers.ThresholdIntervalMapper;
import com.knobtviker.thermopile.presentation.utils.predicates.ThresholdOfDayPredicate;

import org.threeten.bp.ZoneOffset;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ThresholdIntervalFactory {

    public static List<ThresholdInterval> emptyDays() {
        return IntStream.range(0, 6)
            .mapToObj(day ->
                ThresholdInterval.builder()
                    .interval(
                        Interval.of(
                            DateTimeKit.from(day, 0,0).toInstant(ZoneOffset.UTC),
                            DateTimeKit.from(day, 23,59).plusSeconds(59).plusNanos(999999999).toInstant(ZoneOffset.UTC)
                        )
                    )
                    .build()
            )
            .collect(Collectors.toList()
            );
    }

    public static ThresholdInterval emptyDay(final int day) {
        return ThresholdInterval.builder()
            .interval(
                Interval.of(
                    DateTimeKit.from(day, 0,0).toInstant(ZoneOffset.UTC),
                    DateTimeKit.from(day, 23,59).plusSeconds(59).plusNanos(999999999).toInstant(ZoneOffset.UTC)
                )
            )
            .build();
    }

    public static ThresholdIntervalMapper mapper() {
        return ThresholdIntervalMapper.create();
    }

    public static Comparator<? super ThresholdInterval> sorter() {
        return ThresholdIntervalComparator.create();
    }

    public static Predicate<? super Threshold> withDay(final int day) {
        return ThresholdOfDayPredicate.create(day);
    }

    public static Comparator<? super Threshold> sortDay() {
        return ThresholdInDayComparator.create();
    }
}
