package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.knobtviker.thermopile.di.components.data.DaggerAtmosphereDataComponent;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.presentation.contracts.ChartsContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Created by bojan on 15/07/2017.
 */

public class ChartsPresenter extends AbstractPresenter implements ChartsContract.Presenter {

    private final ChartsContract.View view;

    private final AtmosphereRepository atmosphereRepository;

    public ChartsPresenter(@NonNull final ChartsContract.View view) {
        super(view);

        this.view = view;
        this.atmosphereRepository = DaggerAtmosphereDataComponent.create().repository();
    }

    @Override
    public void data(int type, int interval) {
        started();

        final Interval timeInterval = intervalForType(interval);

        switch (type) {
            case 0:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadTemperatureBetween(timeInterval.getStartMillis(), timeInterval.getEndMillis())
                        .subscribe(
                            view::onTemperature,
                            this::error,
                            this::completed
                        )
                );
                break;
            case 1:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadHumidityBetween(timeInterval.getStartMillis(), timeInterval.getEndMillis())
                        .subscribe(
                            view::onHumidity,
                            this::error,
                            this::completed
                        )
                );
                break;
            case 2:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadPressureBetween(timeInterval.getStartMillis(), timeInterval.getEndMillis())
                        .subscribe(
                            view::onPressure,
                            this::error,
                            this::completed
                        )
                );
                break;
            case 3:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadAirQualityBetween(timeInterval.getStartMillis(), timeInterval.getEndMillis())
                        .subscribe(
                            view::onAirQuality,
                            this::error,
                            this::completed
                        )
                );
                break;
            case 4:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadAccelerationBetween(timeInterval.getStartMillis(), timeInterval.getEndMillis())
                        .subscribe(
                            view::onMotion,
                            this::error,
                            this::completed
                        )
                );
                break;
        }

    }

    //TODO: Last item should be Custom range with Calendar day popup range selection.
    private Interval intervalForType(final int interval) {
        final DateTime now = DateTime.now();
        final DateTime other;

        switch (interval) {
            case 0: //Last 60 minutes
                other = now.minusMinutes(60);
                return new Interval(other, now);
            case 1: //Last 24 hours
                other = now.minusHours(24);
                return new Interval(other, now);
            case 2: //Last 7 days
                other = now.minusDays(7);
                return new Interval(other, now);
            case 3: //Last 30 days
                other = now.minusDays(30);
                return new Interval(other, now);
            case 4: //Last 90 days
                other = now.minusDays(90);
                return new Interval(other, now);
            case 5: //Today
                other = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
                return new Interval(other, now);
            case 6: //Yesterday
                final DateTime today = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
                final DateTime yesterday = today.minusDays(1);
                return new Interval(yesterday, today);
            case 7: //This week
                other = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).withDayOfWeek(0);
                return new Interval(other, now);
            case 8: //Last week
                final DateTime thisWeek = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).withDayOfWeek(0);
                final DateTime lastWeek = thisWeek.minusWeeks(1);
                return new Interval(lastWeek, thisWeek);
            case 9: //This month
                other = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).withDayOfMonth(1);
                return new Interval(other, now);
            case 10: //Last month
                final DateTime thisMonth = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).withDayOfMonth(1);
                final DateTime lastMonth = thisMonth.minusMonths(1);
                return new Interval(lastMonth, thisMonth);
            case 11: //This year
                other = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).withDayOfYear(1);
                return new Interval(other, now);
            case 12: //Last year
                final DateTime thisYear = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).withDayOfYear(1);
                final DateTime lastYear = thisYear.minusYears(1);
                return new Interval(lastYear, thisYear);
            default:
                return new Interval(now.minusMillis(1), now);
        }
    }
}
