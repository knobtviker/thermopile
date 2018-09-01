package com.knobtviker.thermopile.presentation.utils;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.presentation.Interval;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.Temporal;
import org.threeten.bp.temporal.TemporalAccessor;
import org.threeten.bp.zone.TzdbZoneRulesProvider;

import java.util.Set;

public class DateTimeKit {

    public static String format(@NonNull final TemporalAccessor dateTime, @NonNull final String pattern) {
        return DateTimeFormatter
            .ofPattern(pattern)
            .format(dateTime);
    }

    public static String format(final long timestamp, @NonNull final String pattern) {
        return DateTimeFormatter
            .ofPattern(pattern)
            .format(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
    }

    public static String format(final int dayOfWeek, final int hour, final int minute, @NonNull final String formatTime) {
        return DateTimeFormatter
            .ofPattern(formatTime)
            .format(
                LocalDateTime.from(
                    LocalTime.of(hour, minute, 0, 0))
                    .with(ChronoField.DAY_OF_WEEK, dayOfWeek)
            );
    }

    public static long minutesBetween(final int startHour, final int startMinute, final int endHour, final int endMinute) {
        return minutesBetween(
            LocalTime.of(startHour, startMinute),
            LocalTime.of(endHour, endMinute)
        );
    }

    public static long minutesBetween(@NonNull final Temporal start, @NonNull final Temporal end) {
        return ChronoUnit.MINUTES.between(start, end);
    }

    public static ZoneId zoneById(@NonNull final String zoneId) {
        return ZoneId.of(zoneId);
    }

    public static Set<String> zones() {
        return TzdbZoneRulesProvider.getAvailableZoneIds();
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static Instant instant() {
        return Instant.now();
    }

    public static Interval today() {
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime other = now.withHour(0).withMinute(0).withSecond(0).withNano(0);

        return Interval.of(other.toInstant(), now.toInstant());
    }

    public static Interval yesterday() {
        final ZonedDateTime today = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        final ZonedDateTime yesterday = today.minusDays(1);

        return Interval.of(yesterday.toInstant(), today.toInstant());
    }

    public static Interval thisWeek() {
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime other = now.withHour(0).withMinute(0).withSecond(0).withNano(0).with(ChronoField.DAY_OF_WEEK, 1);

        return Interval.of(other.toInstant(), now.toInstant());
    }

    public static Interval lastWeek() {
        final ZonedDateTime thisWeek =
            ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).with(ChronoField.DAY_OF_WEEK, 1);
        final ZonedDateTime lastWeek = thisWeek.minusWeeks(1);

        return Interval.of(lastWeek.toInstant(), thisWeek.toInstant());
    }

    public static Interval thisMonth() {
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime other = now.withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfMonth(1);

        return Interval.of(other.toInstant(), now.toInstant());
    }

    public static Interval lastMonth() {
        final ZonedDateTime thisMonth = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfMonth(1);
        final ZonedDateTime lastMonth = thisMonth.minusMonths(1);

        return Interval.of(lastMonth.toInstant(), thisMonth.toInstant());
    }

    public static Interval thisYear() {
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime other = now.withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfYear(1);
        return Interval.of(other.toInstant(), now.toInstant());
    }

    public static Interval lastYear() {
        final ZonedDateTime thisYear = ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfYear(1);
        final ZonedDateTime lastYear = thisYear.minusYears(1);

        return Interval.of(lastYear.toInstant(), thisYear.toInstant());
    }

    public static Interval lastSecond() {
        final ZonedDateTime now = ZonedDateTime.now();
        return Interval.of(now.minusSeconds(1).toInstant(), now.toInstant());
    }

    public static ZonedDateTime from(final int dayOfWeek, final int hour, final int minute) {
        return ZonedDateTime
            .from(LocalTime.of(hour, minute, 0, 0))
            .with(ChronoField.DAY_OF_WEEK, dayOfWeek);
    }

    public static boolean isStartAfterEnd(final int dayOfWeek,
        final int startHour, final int startMinute,
        final int endHour, final int endMinute) {
        final ZonedDateTime startDateTime = from(dayOfWeek, startHour, startMinute);
        final ZonedDateTime endDateTime = from(dayOfWeek, endHour, endMinute);

        return startDateTime.isAfter(endDateTime);
    }

    public static boolean isStartEqualEnd(final int dayOfWeek,
        final int startHour, final int startMinute,
        final int endHour, final int endMinute) {
        final ZonedDateTime startDateTime = from(dayOfWeek, startHour, startMinute);
        final ZonedDateTime endDateTime = from(dayOfWeek, endHour, endMinute);

        return startDateTime.isEqual(endDateTime);
    }

    public static Interval interval(final int dayOfWeek,
        final int startHour, final int startMinute,
        final int endHour, final int endMinute) {
        final ZonedDateTime startDateTime = from(dayOfWeek, startHour, startMinute);
        final ZonedDateTime endDateTime = from(dayOfWeek, endHour, endMinute);

        return Interval.of(startDateTime.toInstant(), endDateTime.toInstant());
    }

    public static int dayOfWeek(@NonNull final Interval interval) {
        return interval.getStart().get(ChronoField.DAY_OF_WEEK);
    }

    public static long secondsToMinutes(final int seconds) {
        return Duration.of(seconds, ChronoUnit.SECONDS).toMinutes();
    }
}
