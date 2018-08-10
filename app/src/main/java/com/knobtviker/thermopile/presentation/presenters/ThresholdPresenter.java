package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.di.components.domain.repositories.DaggerSettingsRepositoryComponent;
import com.knobtviker.thermopile.di.components.domain.repositories.DaggerThresholdRepositoryComponent;
import com.knobtviker.thermopile.di.modules.data.sources.local.SettingsLocalDataSourceModule;
import com.knobtviker.thermopile.di.modules.data.sources.local.ThresholdLocalDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.presentation.contracts.ThresholdContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import io.reactivex.Completable;

/**
 * Created by bojan on 29/10/2017.
 */

public class ThresholdPresenter extends AbstractPresenter implements ThresholdContract.Presenter {

    private final ThresholdContract.View view;


    private final SettingsRepository settingsRepository;
    private final ThresholdRepository thresholdRepository;

    public ThresholdPresenter(@NonNull final ThresholdContract.View view) {
        super(view);

        this.view = view;
        this.settingsRepository = DaggerSettingsRepositoryComponent.builder()
            .localDataSource(new SettingsLocalDataSourceModule())
            .build()
            .inject();
        this.thresholdRepository = DaggerThresholdRepositoryComponent.builder()
            .localDataSource(new ThresholdLocalDataSourceModule())
            .build()
            .inject();
    }

    @Override
    public void settings() {
        compositeDisposable.add(
            settingsRepository
                .observe()
                .subscribe(
                    view::onSettingsChanged,
                    this::error
                )
        );
    }

    @Override
    public void loadById(long thresholdId) {
        compositeDisposable.add(
            thresholdRepository
                .loadById(thresholdId)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    view::onThreshold,
                    this::error
                )
        );
    }

    @Override
    public void save(@NonNull Threshold threshold) {
        compositeDisposable.add(
            Completable.create(emitter -> {
                if (!emitter.isDisposed()) {
                    final DateTime startDateTime = new DateTime()
                        .withDayOfWeek(threshold.day + 1)
                        .withHourOfDay(threshold.startHour)
                        .withMinuteOfHour(threshold.startMinute)
                        .withSecondOfMinute(0)
                        .withMillisOfSecond(0);

                    final DateTime endDateTime = new DateTime()
                        .withDayOfWeek(threshold.day + 1)
                        .withHourOfDay(threshold.endHour)
                        .withMinuteOfHour(threshold.endMinute)
                        .withSecondOfMinute(0)
                        .withMillisOfSecond(0);

                    if (startDateTime.isAfter(endDateTime)) {
                        emitter.onError(new Throwable("Start time cannot be after end time"));
                    } else if (startDateTime.isEqual(endDateTime)) {
                        emitter.onError(new Throwable("Start time cannot be equal and exact as end time"));
                    } else {
                        final Interval interval = new Interval(startDateTime, endDateTime);
                        if (interval.toDuration().toStandardHours().getHours() >= 1) {
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Throwable("Duration between start and end time is less than one hour"));
                        }
                    }
                }
            })
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .andThen(thresholdRepository.save(threshold))
                .ignoreElements()
                .subscribe(
                    view::onSaved,
                    this::error
                )
        );
    }
}
