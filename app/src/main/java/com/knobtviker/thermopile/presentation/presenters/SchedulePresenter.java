package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.data.models.presentation.ThresholdChip;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.ScheduleContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;
import com.knobtviker.thermopile.presentation.utils.MathKit;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.internal.functions.Functions;

/**
 * Created by bojan on 15/07/2017.
 */

public class SchedulePresenter extends AbstractPresenter<ScheduleContract.View> implements ScheduleContract.Presenter {

    @NonNull
    @FormatDate
    private String formatDate = FormatDate.EEEE_DD_MM_YYYY;

    @UnitTemperature
    private int unitTemperature = UnitTemperature.CELSIUS;

    @NonNull
    private List<Threshold> thresholds = Collections.emptyList();

    @NonNull
    private final SettingsRepository settingsRepository;

    @NonNull
    private final ThresholdRepository thresholdRepository;

    @Inject
    public SchedulePresenter(
        @NonNull final ScheduleContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final ThresholdRepository thresholdRepository,
        @NonNull final Schedulers schedulers
    ) {
        super(view, schedulers);
        this.settingsRepository = settingsRepository;
        this.thresholdRepository = thresholdRepository;
    }

    @Override
    public void load() {
        setWeekdayNames();

        settings();
    }

    @Override
    public void removeThresholdById(long id) {
        compositeDisposable.add(
            thresholdRepository
                .removeById(id)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    private void settings() {
        compositeDisposable.add(
            settingsRepository
                .observe()
                .doOnNext(item -> thresholds())
                .subscribe(
                    this::onSettingsChanged,
                    this::error
                )
        );
    }

    private void thresholds() {
        compositeDisposable.add(
            thresholdRepository
                .load()
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    this::onThresholds,
                    this::error
                )
        );
    }

    private void onSettingsChanged(@NonNull final Settings settings) {
        if (!formatDate.equals(settings.formatDate)) {
            formatDate = settings.formatDate;

            setWeekdayNames();
        }
        if (unitTemperature != settings.unitTemperature) {
            unitTemperature = settings.unitTemperature;
        }
    }

    //TODO: Move map into mapper local to presentation method
    private void onThresholds(@NonNull final List<Threshold> thresholds) {
        view.onThresholds(
            thresholds.stream()
                .map(item -> ThresholdChip.builder()
                    .id(item.id)
                    .day(item.day)
                    .width(calculateThresholdWidth(item.startHour, item.startMinute, item.endHour, item.endMinute))
                    .offset(calculateThresholdOffset(item.startHour, item.startMinute))
                    .temperature(buildThresholdTemperature(item.temperature))
                    .color(item.color)
                    .build()
                )
                .collect(Collectors.toList())
        );
    }

    private void setWeekdayNames() {
        if (formatDate.contains("EEEE")) {
            view.setWeekdayNames(false);
        } else if (formatDate.contains("EE")) {
            view.setWeekdayNames(true);
        } else {
            view.setWeekdayNames(false);
        }
    }

    private String buildThresholdTemperature(final int value) {
        @StringRes
        int unit;

        switch (unitTemperature) {
            case UnitTemperature.CELSIUS:
                unit = R.string.unit_temperature_celsius;
                break;
            case UnitTemperature.FAHRENHEIT:
                unit = R.string.unit_temperature_fahrenheit;
                break;
            case UnitTemperature.KELVIN:
                unit = R.string.unit_temperature_kelvin;
                break;
            default:
                unit = R.string.unit_temperature_celsius;
                break;
        }

        return String.format("%s %s", String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, value))), unit);
    }

    private int calculateThresholdWidth(final int startHour, final int startMinute, final int endHour, final int endMinute) {
        return Math.round(
            DateTimeKit.minutesBetween(startHour, startMinute, endHour, endMinute) / 2.0f //TODO: Investigate and remove this magic number.
        );
    }

    private int calculateThresholdOffset(final int startHour, final int startMinute) {
        return Math.round((startHour * 60 + startMinute) / 2.0f); //TODO: Investigate and remove this magic number.
    }
}
