package com.knobtviker.thermopile.presentation.utils.factories;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.models.presentation.Interval;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;
import com.knobtviker.thermopile.presentation.utils.comparators.ThresholdInDayComparator;
import com.knobtviker.thermopile.presentation.utils.comparators.ThresholdIntervalComparator;
import com.knobtviker.thermopile.presentation.utils.mappers.ThresholdIntervalMapper;
import com.knobtviker.thermopile.presentation.utils.predicates.ThresholdOfDayPredicate;

import org.threeten.bp.LocalTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.temporal.ChronoField;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ThresholdIntervalFactory {

    public static List<ThresholdInterval> emptyDays() {
        return IntStream.range(1, 8)
            .mapToObj(day ->
                ThresholdInterval.builder()
                    .interval(
                        Interval.of(
                            ZonedDateTime.from(
                                LocalTime.of(0, 0, 0, 0))
                                .with(ChronoField.DAY_OF_WEEK, day)
                                .toInstant(),
                            ZonedDateTime.from(
                                LocalTime.of(23, 59, 59, 999999999))
                                .with(ChronoField.DAY_OF_WEEK, day)
                                .toInstant()
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
                    ZonedDateTime.from(
                        LocalTime.of(0, 0, 0, 0))
                        .with(ChronoField.DAY_OF_WEEK, day)
                        .toInstant(),
                    ZonedDateTime.from(
                        LocalTime.of(23, 59, 59, 999999999))
                        .with(ChronoField.DAY_OF_WEEK, day)
                        .toInstant()
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
