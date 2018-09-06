package com.knobtviker.thermopile.domain.repositories;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.models.presentation.Interval;
import com.knobtviker.thermopile.data.models.presentation.ThresholdInterval;
import com.knobtviker.thermopile.data.sources.local.ThresholdLocalDataSource;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.domain.shared.base.AbstractRepository;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;
import com.knobtviker.thermopile.presentation.utils.factories.ThresholdIntervalFactory;

import org.threeten.bp.ZoneOffset;

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

    @NonNull
    private final ThresholdLocalDataSource thresholdLocalDataSource;

    @Inject
    public ThresholdRepository(
        @NonNull final ThresholdLocalDataSource thresholdLocalDataSource,
        @NonNull final Schedulers schedulers
    ) {
        super(schedulers);
        this.thresholdLocalDataSource = thresholdLocalDataSource;
    }

    public Observable<List<Threshold>> load() {
        return thresholdLocalDataSource
            .query()
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui);
    }

    public Observable<List<ThresholdInterval>> loadInline() {
        return thresholdLocalDataSource
            .query()
            .subscribeOn(schedulers.computation)
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
            .observeOn(schedulers.ui);
    }

    public Observable<Threshold> loadById(final long thresholdId) {
        return thresholdLocalDataSource
            .queryById(thresholdId)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui);
    }

    public Observable<Long> save(@NonNull final Threshold item) {
        return thresholdLocalDataSource
            .save(item)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui);
    }

    public Completable removeById(final long id) {
        return thresholdLocalDataSource
            .removeById(id)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui);
    }

    private void fillDayWithThresholds(@NonNull final List<Threshold> thresholds, @NonNull final List<ThresholdInterval> thresholdIntervals,
        final int day) {
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
                            Interval.of(
                                DateTimeKit.from(first.day, 0, 0).toInstant(ZoneOffset.UTC),
                                DateTimeKit.from(first.day, first.startHour, first.startMinute).minusNanos(1).toInstant(ZoneOffset.UTC)
                            )
                        )
                        .build()
                );
            }
            if (last.endHour != 23 || last.endMinute != 59) {
                thresholdIntervals.add(
                    ThresholdInterval.builder()
                        .interval(
                            Interval.of(
                                DateTimeKit.from(first.day, last.endHour, last.endMinute).minusNanos(1).toInstant(ZoneOffset.UTC),
                                DateTimeKit.from(first.day, 23, 59).plusSeconds(59).plusNanos(999999999).toInstant(ZoneOffset.UTC)
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
                                        Interval.of(
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
