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
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;

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
                    if (DateTimeKit.isStartAfterEnd(
                        threshold.day,
                        threshold.startHour,
                        threshold.startMinute,
                        threshold.endHour,
                        threshold.endMinute)
                    ) {
                        emitter.onError(new Throwable("Start time cannot be after end time"));
                    } else if (DateTimeKit.isStartEqualEnd(
                        threshold.day,
                        threshold.startHour,
                        threshold.startMinute,
                        threshold.endHour,
                        threshold.endMinute)) {
                        emitter.onError(new Throwable("Start time cannot be equal and exact as end time"));
                    } else {
                        if (DateTimeKit.interval(
                            threshold.day,
                            threshold.startHour,
                            threshold.startMinute,
                            threshold.endHour,
                            threshold.endMinute
                        ).toDuration().toHours() >= 1) {
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
