package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.ThresholdContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredTemperature;
import com.knobtviker.thermopile.presentation.shared.constants.settings.ClockMode;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;
import com.knobtviker.thermopile.presentation.utils.MathKit;

import javax.inject.Inject;

import io.reactivex.Completable;

/**
 * Created by bojan on 29/10/2017.
 */

public class ThresholdPresenter extends AbstractPresenter<ThresholdContract.View> implements ThresholdContract.Presenter {

    @ClockMode
    private int formatClock = ClockMode._24H;

    @NonNull
    @FormatTime
    private String formatTime = FormatTime.HH_MM;

    @UnitTemperature
    private int unitTemperature = UnitTemperature.CELSIUS;

    @NonNull
    SettingsRepository settingsRepository;

    @NonNull
    ThresholdRepository thresholdRepository;

    @Inject
    public ThresholdPresenter(
        @NonNull final ThresholdContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final ThresholdRepository thresholdRepository,
        @NonNull final Schedulers schedulers
    ) {
        super(view, schedulers);
        this.settingsRepository = settingsRepository;
        this.thresholdRepository = thresholdRepository;
    }

    @Override
    public void setStartTime(int day, int hour, int minute) {
        view.onStartTimeChanged(DateTimeKit.format(day, hour, minute, formatTime));
    }

    @Override
    public void setEndTime(int day, int hour, int minute) {
        view.onEndTimeChanged(DateTimeKit.format(day, hour, minute, formatTime));
    }

    @Override
    public void loadById(long thresholdId) {
        compositeDisposable.add(
            settingsRepository
                .load()
                .subscribe(
                    this::onSettingsChanged,
                    this::error,
                    () ->
                        compositeDisposable.add(
                            thresholdRepository
                                .loadById(thresholdId)
                                .doOnSubscribe(consumer -> subscribed())
                                .doOnTerminate(this::terminated)
                                .subscribe(
                                    view::onThreshold,
                                    this::error
                                )
                        )
                )
        );
    }

    @Override
    public void createNew(int day, int minute, int maxWidth) {
        compositeDisposable.add(
            settingsRepository
                .load()
                .subscribe(
                    this::onSettingsChanged,
                    this::error,
                    () -> {
                        //TODO: Investigate 1440 magic number wtf
                        final int startMinuteInADay = Math.round(minute * (1440 / (float) maxWidth));

                        final int startHour = startMinuteInADay / 60;
                        final int startMinute = startMinuteInADay - startHour * 60;

                        view.updateDialogStartTime(startHour, startMinute);

                        setStartTime(day + 1, startHour, startMinute);

                        setEndTime(day + 1, startHour, startMinute);
                    }
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

    private void onSettingsChanged(@NonNull final Settings settings) {
        if (unitTemperature != settings.unitTemperature) {
            unitTemperature = settings.unitTemperature;

            @StringRes
            int resId;

            switch (unitTemperature) {
                case UnitTemperature.CELSIUS:
                    resId = R.string.unit_temperature_celsius;
                    break;
                case UnitTemperature.FAHRENHEIT:
                    resId = R.string.unit_temperature_fahrenheit;
                    break;
                case UnitTemperature.KELVIN:
                    resId = R.string.unit_temperature_kelvin;
                    break;
                default:
                    resId = R.string.unit_temperature_celsius;
                    break;
            }

            view.onTemperatureUnitChanged(
                resId,
                MathKit.applyTemperatureUnit(unitTemperature, MeasuredTemperature.MINIMUM),
                MathKit.applyTemperatureUnit(unitTemperature, MeasuredTemperature.MAXIMUM)
            );
        }

        formatClock = settings.formatClock;
        formatTime = settings.formatTime;

        view.onClockAndTimeFormatChanged(
            formatClock == ClockMode._24H,
            DateTimeKit.format(DateTimeKit.now(), formatTime)
        );
    }
}
