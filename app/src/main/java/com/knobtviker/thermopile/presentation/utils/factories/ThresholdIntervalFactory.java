package com.knobtviker.thermopile.presentation.utils.factories;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;
import com.knobtviker.thermopile.presentation.utils.comparators.ThresholdInDayComparator;
import com.knobtviker.thermopile.presentation.utils.comparators.ThresholdIntervalComparator;
import com.knobtviker.thermopile.presentation.utils.mappers.ThresholdIntervalMapper;
import com.knobtviker.thermopile.presentation.utils.predicates.ThresholdOfDayPredicate;

import org.joda.time.DateTime;
import org.joda.time.Interval;

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
                            new Interval(
                                new DateTime()
                                    .withDayOfWeek(day)
                                    .withHourOfDay(0)
                                    .withMinuteOfHour(0)
                                    .withSecondOfMinute(0)
                                    .withMillisOfSecond(0),
                                new DateTime()
                                    .withDayOfWeek(day)
                                    .withHourOfDay(23)
                                    .withMinuteOfHour(59)
                                    .withSecondOfMinute(59)
                                    .withMillisOfSecond(999)
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
                new Interval(
                    new DateTime()
                        .withDayOfWeek(day + 1)
                        .withHourOfDay(0)
                        .withMinuteOfHour(0)
                        .withSecondOfMinute(0)
                        .withMillisOfSecond(0),
                    new DateTime()
                        .withDayOfWeek(day + 1)
                        .withHourOfDay(23)
                        .withMinuteOfHour(59)
                        .withSecondOfMinute(59)
                        .withMillisOfSecond(999)
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
