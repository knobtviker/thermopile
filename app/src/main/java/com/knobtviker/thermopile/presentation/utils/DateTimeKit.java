package com.knobtviker.thermopile.presentation.utils;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.presentation.Interval;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
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
                LocalDateTime.of(
                    LocalDate.now().with(ChronoField.DAY_OF_WEEK, dayOfWeek+1),
                    LocalTime.of(hour, minute, 0, 0))
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
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime other = now.withHour(0).withMinute(0).withSecond(0).withNano(0);

        return Interval.of(other.toInstant(ZoneOffset.UTC), now.toInstant(ZoneOffset.UTC));
    }

    public static Interval yesterday() {
        final LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        final LocalDateTime yesterday = today.minusDays(1);

        return Interval.of(yesterday.toInstant(ZoneOffset.UTC), today.toInstant(ZoneOffset.UTC));
    }

    public static Interval thisWeek() {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime other = now.withHour(0).withMinute(0).withSecond(0).withNano(0).with(ChronoField.DAY_OF_WEEK, 1);

        return Interval.of(other.toInstant(ZoneOffset.UTC), now.toInstant(ZoneOffset.UTC));
    }

    public static Interval lastWeek() {
        final LocalDateTime thisWeek =
            LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).with(ChronoField.DAY_OF_WEEK, 1);
        final LocalDateTime lastWeek = thisWeek.minusWeeks(1);

        return Interval.of(lastWeek.toInstant(ZoneOffset.UTC), thisWeek.toInstant(ZoneOffset.UTC));
    }

    public static Interval thisMonth() {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime other = now.withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfMonth(1);

        return Interval.of(other.toInstant(ZoneOffset.UTC), now.toInstant(ZoneOffset.UTC));
    }

    public static Interval lastMonth() {
        final LocalDateTime thisMonth = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfMonth(1);
        final LocalDateTime lastMonth = thisMonth.minusMonths(1);

        return Interval.of(lastMonth.toInstant(ZoneOffset.UTC), thisMonth.toInstant(ZoneOffset.UTC));
    }

    public static Interval thisYear() {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime other = now.withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfYear(1);
        return Interval.of(other.toInstant(ZoneOffset.UTC), now.toInstant(ZoneOffset.UTC));
    }

    public static Interval lastYear() {
        final LocalDateTime thisYear = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfYear(1);
        final LocalDateTime lastYear = thisYear.minusYears(1);

        return Interval.of(lastYear.toInstant(ZoneOffset.UTC), thisYear.toInstant(ZoneOffset.UTC));
    }

    public static Interval lastSecond() {
        final LocalDateTime now = LocalDateTime.now();
        return Interval.of(now.minusSeconds(1).toInstant(ZoneOffset.UTC), now.toInstant(ZoneOffset.UTC));
    }

    public static LocalDateTime from(final int dayOfWeek, final int hour, final int minute) {
        return LocalDateTime.of(
            LocalDate.now().with(ChronoField.DAY_OF_WEEK, dayOfWeek+1),
            LocalTime.of(hour, minute, 0, 0)
        );
    }

    public static boolean isStartAfterEnd(final int dayOfWeek,
        final int startHour, final int startMinute,
        final int endHour, final int endMinute) {
        final LocalDateTime startDateTime = from(dayOfWeek+1, startHour, startMinute);
        final LocalDateTime endDateTime = from(dayOfWeek+1, endHour, endMinute);

        return startDateTime.isAfter(endDateTime);
    }

    public static boolean isStartEqualEnd(final int dayOfWeek,
        final int startHour, final int startMinute,
        final int endHour, final int endMinute) {
        final LocalDateTime startDateTime = from(dayOfWeek+1, startHour, startMinute);
        final LocalDateTime endDateTime = from(dayOfWeek+1, endHour, endMinute);

        return startDateTime.isEqual(endDateTime);
    }

    public static Interval interval(final int dayOfWeek,
        final int startHour, final int startMinute,
        final int endHour, final int endMinute) {
        final LocalDateTime startDateTime = from(dayOfWeek+1, startHour, startMinute);
        final LocalDateTime endDateTime = from(dayOfWeek+1, endHour, endMinute);

        return Interval.of(startDateTime.toInstant(ZoneOffset.UTC), endDateTime.toInstant(ZoneOffset.UTC));
    }

    public static int dayOfWeek(@NonNull final Interval interval) {
        return LocalDateTime.ofInstant(interval.getStart(), ZoneId.systemDefault()).toLocalDate().get(ChronoField.DAY_OF_WEEK);
    }

    public static long secondsToMinutes(final int seconds) {
        return Duration.of(seconds, ChronoUnit.SECONDS).toMinutes();
    }
}
