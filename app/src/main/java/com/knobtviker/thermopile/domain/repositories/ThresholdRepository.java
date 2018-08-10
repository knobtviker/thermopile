package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;
import com.knobtviker.thermopile.data.sources.local.ThresholdLocalDataSource;
import com.knobtviker.thermopile.domain.shared.base.AbstractRepository;
import com.knobtviker.thermopile.presentation.utils.factories.ThresholdIntervalFactory;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;


/**
 * Created by bojan on 17/07/2017.
 */

public class ThresholdRepository extends AbstractRepository {

    @Inject
    ThresholdLocalDataSource thresholdLocalDataSource;

    @Inject
    ThresholdRepository() {
    }

    public Observable<List<Threshold>> load() {
        return thresholdLocalDataSource
            .query()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<List<ThresholdInterval>> loadInline() {
        return thresholdLocalDataSource
            .query()
            .subscribeOn(schedulerProvider.io)
            .map(thresholds -> {
                if (thresholds.isEmpty()) {
                    return ThresholdIntervalFactory.emptyDays();
                } else {
                    final List<ThresholdInterval> intervalsAll = thresholds
                        .stream()
                        .map(ThresholdIntervalFactory.mapper())
                        .collect(Collectors.toList());

                    IntStream.range(0, 7).forEach(day -> fillDayWithThresholds(thresholds, intervalsAll, day));

                    intervalsAll.sort(ThresholdIntervalFactory.sorter());

                    return intervalsAll;
                }
            })
            .observeOn(schedulerProvider.ui);
    }

    public Observable<Threshold> loadById(final long thresholdId) {
        return thresholdLocalDataSource
            .queryById(thresholdId)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Observable<Long> save(@NonNull final Threshold item) {
        return thresholdLocalDataSource
            .save(item)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    public Completable removeById(final long id) {
        return thresholdLocalDataSource
            .removeById(id)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui);
    }

    private void fillDayWithThresholds(@NonNull final List<Threshold> thresholds, @NonNull final List<ThresholdInterval> thresholdIntervals, final int day) {
        final List<Threshold> thresholdsInDay = thresholds.stream()
            .filter(ThresholdIntervalFactory.withDay(day)) //Filter thresholds only for that day
            .sorted(ThresholdIntervalFactory.sortDay())
            .collect(Collectors.toList()); //Collect them in a list

        if (!thresholdsInDay.isEmpty()) { //Day has thresholds
            final Threshold first = thresholdsInDay.get(0);
            final Threshold last = thresholdsInDay.get(thresholdsInDay.size() - 1);

            if (first.startHour != 0 || first.startMinute != 0) {
                thresholdIntervals.add(
                    ThresholdInterval.builder()
                        .interval(
                            new Interval(
                                new DateTime()
                                    .withDayOfWeek(first.day + 1)
                                    .withHourOfDay(0)
                                    .withMinuteOfHour(0)
                                    .withSecondOfMinute(0)
                                    .withMillisOfSecond(0),
                                new DateTime()
                                    .withDayOfWeek(first.day + 1)
                                    .withHourOfDay(first.startHour)
                                    .withMinuteOfHour(first.startMinute)
                                    .withSecondOfMinute(0)
                                    .withMillisOfSecond(0)
                                    .minusMillis(1)
                            )
                        )
                        .build()
                );
            }
            if (last.endHour != 23 || last.endMinute != 59) {
                thresholdIntervals.add(
                    ThresholdInterval.builder()
                        .interval(
                            new Interval(
                                new DateTime()
                                    .withDayOfWeek(first.day + 1)
                                    .withHourOfDay(last.endHour)
                                    .withMinuteOfHour(last.endMinute)
                                    .withSecondOfMinute(0)
                                    .withMillisOfSecond(0)
                                    .minusMillis(1),
                                new DateTime()
                                    .withDayOfWeek(first.day + 1)
                                    .withHourOfDay(23)
                                    .withMinuteOfHour(59)
                                    .withSecondOfMinute(59)
                                    .withMillisOfSecond(999)
                            )
                        )
                        .build()
                );
            }

            if (thresholdsInDay.size() > 1) {
                IntStream.range(0, thresholdsInDay.size() - 1)
                    .forEach(index -> {
                        final Interval current = thresholdIntervals.get(index).interval();
                        final Interval next = thresholdIntervals.get(index + 1).interval();
                        final Interval gap = current.gap(next);
                        if (gap != null) {
                            thresholdIntervals.add(
                                ThresholdInterval.builder()
                                    .interval(
                                        new Interval(
                                            gap.getStart().plusMillis(1),
                                            gap.getEnd().minusMillis(1)
                                        )
                                    )
                                    .build()
                            );
                        }
                    });
            }
        } else { //Day has no thresholds, build an empty day interval
            thresholdIntervals.add(ThresholdIntervalFactory.emptyDay(day));
        }
    }
}
