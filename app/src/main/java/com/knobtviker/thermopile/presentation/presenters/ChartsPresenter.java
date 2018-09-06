package com.knobtviker.thermopile.presentation.presenters;

import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.AirQuality;
import com.knobtviker.thermopile.data.models.local.Humidity;
import com.knobtviker.thermopile.data.models.local.Motion;
import com.knobtviker.thermopile.data.models.local.Pressure;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.models.local.shared.SingleModel;
import com.knobtviker.thermopile.data.models.presentation.Interval;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.ChartsContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;
import com.knobtviker.thermopile.presentation.shared.constants.charts.ChartInterval;
import com.knobtviker.thermopile.presentation.shared.constants.charts.ChartType;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredAirQuality;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredHumidity;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredPressure;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredTemperature;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitPressure;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;
import com.knobtviker.thermopile.presentation.utils.MathKit;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Created by bojan on 15/07/2017.
 */

public class ChartsPresenter extends AbstractPresenter<ChartsContract.View> implements ChartsContract.Presenter {

    @NonNull
    @FormatDate
    private String formatDate = FormatDate.EEEE_DD_MM_YYYY;

    @NonNull
    @FormatTime
    private String formatTime = FormatTime.HH_MM;

    @UnitTemperature
    private int unitTemperature = UnitTemperature.CELSIUS;

    @UnitPressure
    private int unitPressure = UnitPressure.PASCAL;

    @UnitAcceleration
    private int unitMotion = UnitAcceleration.METERS_PER_SECOND_2;

    @ChartType
    private int type = ChartType.TEMPERATURE;

    @StringRes
    private int labelTemperatureUnit = R.string.unit_temperature_celsius;

    @StringRes
    private int labelPressureUnit = R.string.unit_pressure_pascal;

    @StringRes
    private int labelMotionUnit = R.string.unit_acceleration_ms2;

    @NonNull
    private Interval interval = DateTimeKit.today();

    @NonNull
    private final SettingsRepository settingsRepository;

    @NonNull
    private final AtmosphereRepository atmosphereRepository;

    @Inject
    public ChartsPresenter(
        @NonNull final ChartsContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final AtmosphereRepository atmosphereRepository,
        @NonNull final Schedulers schedulers
    ) {
        super(view, schedulers);
        this.settingsRepository = settingsRepository;
        this.atmosphereRepository = atmosphereRepository;
    }

    @Override
    public void settings() {
        compositeDisposable.add(
            settingsRepository
                .observe()
                .subscribe(
                    this::onSettingsChanged,
                    this::error
                )
        );
    }

    @Override
    public void data() {
        final long startTimestamp = interval.getStart().toEpochMilli();
        final long endTimestamp = interval.getEnd().toEpochMilli();

        switch (type) {
            case ChartType.TEMPERATURE:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadTemperatureBetween(startTimestamp, endTimestamp)
                        .doOnSubscribe(consumer -> subscribed())
                        .doOnTerminate(this::terminated)
                        .subscribe(
                            this::onTemperatureLoaded,
                            this::error
                        )
                );
                break;
            case ChartType.HUMIDITY:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadHumidityBetween(startTimestamp, endTimestamp)
                        .doOnSubscribe(consumer -> subscribed())
                        .doOnTerminate(this::terminated)
                        .subscribe(
                            this::onHumidityLoaded,
                            this::error
                        )
                );
                break;
            case ChartType.PRESSURE:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadPressureBetween(startTimestamp, endTimestamp)
                        .doOnSubscribe(consumer -> subscribed())
                        .doOnTerminate(this::terminated)
                        .subscribe(
                            this::onPressureLoaded,
                            this::error
                        )
                );
                break;
            case ChartType.AIR_QUALITY:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadAirQualityBetween(startTimestamp, endTimestamp)
                        .doOnSubscribe(consumer -> subscribed())
                        .doOnTerminate(this::terminated)
                        .subscribe(
                            this::onAirQualityLoaded,
                            this::error
                        )
                );
                break;
            case ChartType.MOTION:
                compositeDisposable.add(
                    atmosphereRepository
                        .loadAccelerationBetween(startTimestamp, endTimestamp)
                        .doOnSubscribe(consumer -> subscribed())
                        .doOnTerminate(this::terminated)
                        .map(data -> data
                            .stream()
                            .map(item ->
                                Motion.create(
                                    item.id,
                                    item.vendor,
                                    item.name,
                                    ((float) Math.min(Math.sqrt(
                                        Math.pow(item.valueX, 2) + Math.pow(item.valueY, 2) + Math
                                            .pow(item.valueZ + SensorManager.GRAVITY_EARTH, 2)),
                                        MeasuredAcceleration.MAXIMUM)),
                                    item.timestamp
                                ))
                            .filter(item -> (item.value >= MeasuredAcceleration.MINIMUM && item.value <= MeasuredAcceleration.MAXIMUM))
                            .collect(Collectors.toList())
                        )
                        .subscribe(
                            this::onMotionLoaded,
                            this::error
                        )
                );
                break;
        }
    }

    @Override
    public void type(@ChartType int value) {
        type = value;

        data();
    }

    @Override
    public void interval(int position) {
        switch (position) {
            case ChartInterval.TODAY:
                interval = DateTimeKit.today();
                break;
            case ChartInterval.YESTERDAY:
                interval = DateTimeKit.yesterday();
                break;
            case ChartInterval.THIS_WEEK:
                interval = DateTimeKit.thisWeek();
                break;
            case ChartInterval.LAST_WEEK:
                interval = DateTimeKit.lastWeek();
                break;
            case ChartInterval.THIS_MONTH:
                interval = DateTimeKit.thisMonth();
                break;
            case ChartInterval.LAST_MONTH:
                interval = DateTimeKit.lastMonth();
                break;
            case ChartInterval.THIS_YEAR:
                interval = DateTimeKit.thisYear();
                break;
            case ChartInterval.LAST_YEAR:
                interval = DateTimeKit.lastYear();
                break;
            default:
                interval = DateTimeKit.lastSecond();
                break;
        }

        data();
    }

    @Override
    public void scrub(@Nullable Object value) {
        if (value == null) {
            view.noScrubbedValue();
        } else {
            final SingleModel object = (SingleModel) value;
            switch (type) {
                case ChartType.TEMPERATURE:
                    view.hasScrubbedValue(
                        String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, object.value))),
                        labelTemperatureUnit,
                        DateTimeKit.format(object.timestamp, String.format("%s %s", formatDate, formatTime))
                    );
                    break;
                case ChartType.HUMIDITY:
                    view.hasScrubbedValue(
                        String.valueOf(MathKit.round(object.value)),
                        R.string.unit_humidity_percent,
                        DateTimeKit.format(object.timestamp, String.format("%s %s", formatDate, formatTime))
                    );
                    break;
                case ChartType.PRESSURE:
                    view.hasScrubbedValue(
                        String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, object.value))),
                        labelPressureUnit,
                        DateTimeKit.format(object.timestamp, String.format("%s %s", formatDate, formatTime))
                    );
                    break;
                case ChartType.AIR_QUALITY:
                    view.hasScrubbedValue(
                        MathKit.convertIAQValueToLabel(object.value),
                        R.string.label_empty,
                        DateTimeKit.format(object.timestamp, String.format("%s %s", formatDate, formatTime))
                    );
                    break;
                case ChartType.MOTION:
                    view.hasScrubbedValue(
                        String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, object.value))),
                        labelMotionUnit,
                        DateTimeKit.format(object.timestamp, String.format("%s %s", formatDate, formatTime))
                    );
                    break;
            }
        }
    }

    private void onSettingsChanged(@NonNull final Settings settings) {
        formatDate = settings.formatDate;
        formatTime = settings.formatTime;
        unitTemperature = settings.unitTemperature;
        unitPressure = settings.unitPressure;
        unitMotion = settings.unitMotion;

        setTemperatureUnit();
        setPressureUnit();
        setMotionUnit();

        switch (type) {
            case ChartType.TEMPERATURE:
                view.onMinMax(
                    labelTemperatureUnit,
                    String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, MeasuredTemperature.MAXIMUM))),
                    String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, MeasuredTemperature.MINIMUM)))
                );
                break;
            case ChartType.HUMIDITY:
                view.onMinMax(
                    R.string.unit_humidity_percent,
                    String.valueOf(MathKit.round(MeasuredHumidity.MAXIMUM)),
                    String.valueOf(MathKit.round(MeasuredHumidity.MINIMUM))
                );
                break;
            case ChartType.PRESSURE:
                view.onMinMax(
                    labelPressureUnit,
                    String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, MeasuredPressure.MAXIMUM))),
                    String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, MeasuredPressure.MINIMUM)))
                );
                break;
            case ChartType.AIR_QUALITY:
                //TODO: Fix this hardcoded string. Should be from strings.xml
                view.onMinMax(
                    R.string.label_empty,
                    "Good",
                    "Very bad"
                );
                break;
            case ChartType.MOTION:
                view.onMinMax(
                    labelMotionUnit,
                    String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, MeasuredAcceleration.MAXIMUM))),
                    String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, MeasuredAcceleration.MINIMUM)))
                );
                break;
        }
    }

    private void onTemperatureLoaded(@NonNull final List<Temperature> data) {
        view.onMinMax(
            labelTemperatureUnit,
            String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, MeasuredTemperature.MAXIMUM))),
            String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, MeasuredTemperature.MINIMUM)))
        );

        final Optional<Temperature> optional = data
            .stream()
            .filter(item -> item.value <= MeasuredTemperature.MAXIMUM)
            .max((item, other) -> Float.compare(item.value, other.value));

        view.applyTopPadding(optional.map(item -> item.value).orElse(MeasuredTemperature.MAXIMUM), MeasuredTemperature.MAXIMUM);

        view.onTemperature(data);
    }

    private void onHumidityLoaded(@NonNull final List<Humidity> data) {
        view.onMinMax(
            R.string.unit_humidity_percent,
            String.valueOf(MathKit.round(MeasuredHumidity.MAXIMUM)),
            String.valueOf(MathKit.round(MeasuredHumidity.MINIMUM))
        );

        final Optional<Humidity> optional = data
            .stream()
            .filter(item -> item.value <= MeasuredHumidity.MAXIMUM)
            .max((item, other) -> Float.compare(item.value, other.value));

        view.applyTopPadding(optional.map(item -> item.value).orElse(MeasuredHumidity.MAXIMUM), MeasuredHumidity.MAXIMUM);

        view.onHumidity(data);
    }

    private void onPressureLoaded(@NonNull final List<Pressure> data) {
        view.onMinMax(
            labelPressureUnit,
            String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, MeasuredPressure.MAXIMUM))),
            String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, MeasuredPressure.MINIMUM)))
        );

        final Optional<Pressure> optional = data
            .stream()
            .filter(item -> item.value <= MeasuredPressure.MAXIMUM)
            .max((item, other) -> Float.compare(item.value, other.value));

        view.applyTopPadding(optional.map(item -> item.value).orElse(MeasuredPressure.MAXIMUM), MeasuredPressure.MAXIMUM);

        view.onPressure(data);
    }

    private void onAirQualityLoaded(@NonNull final List<AirQuality> data) {
        //TODO: Fix this hardcoded string. Should be from strings.xml
        view.onMinMax(
            R.string.label_empty,
            "Good",
            "Very bad"
        );

        data.forEach(item -> item.value = ((MeasuredAirQuality.MAXIMUM - item.value) / MeasuredAirQuality.MAXIMUM));

        final Optional<AirQuality> optional = data
            .stream()
            .filter(item -> item.value <= MeasuredAirQuality.MAXIMUM)
            .max((item, other) -> Float.compare(item.value, other.value));

        view.applyTopPadding(optional.map(item -> (MeasuredAirQuality.MAXIMUM - item.value)).orElse(MeasuredAirQuality.MAXIMUM),
            MeasuredAirQuality.MAXIMUM);

        view.onAirQuality(data);
    }

    private void onMotionLoaded(@NonNull final List<Motion> data) {
        view.onMinMax(
            labelMotionUnit,
            String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, MeasuredAcceleration.MAXIMUM))),
            String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, MeasuredAcceleration.MINIMUM)))
        );

        final Optional<Motion> optional = data
            .stream()
            .filter(item -> item.value <= MeasuredAcceleration.MAXIMUM)
            .max((item, other) -> Float.compare(item.value, other.value));

        view.applyTopPadding(optional.map(item -> item.value).orElse(MeasuredAcceleration.MAXIMUM), MeasuredAcceleration.MAXIMUM);

        view.onMotion(data);
    }

    private void setTemperatureUnit() {
        switch (unitTemperature) {
            case UnitTemperature.CELSIUS:
                labelTemperatureUnit = R.string.unit_temperature_celsius;
                break;
            case UnitTemperature.FAHRENHEIT:
                labelTemperatureUnit = R.string.unit_temperature_fahrenheit;
                break;
            case UnitTemperature.KELVIN:
                labelTemperatureUnit = R.string.unit_temperature_kelvin;
                break;
            default:
                labelTemperatureUnit = R.string.unit_temperature_celsius;
                break;
        }
    }

    private void setPressureUnit() {
        switch (unitPressure) {
            case UnitPressure.PASCAL:
                labelPressureUnit = R.string.unit_pressure_pascal;
                break;
            case UnitPressure.BAR:
                labelPressureUnit = R.string.unit_pressure_bar;
                break;
            case UnitPressure.PSI:
                labelPressureUnit = R.string.unit_pressure_psi;
                break;
            default:
                labelPressureUnit = R.string.unit_pressure_pascal;
                break;
        }
    }

    private void setMotionUnit() {
        switch (unitMotion) {
            case UnitAcceleration.METERS_PER_SECOND_2:
                labelMotionUnit = R.string.unit_acceleration_ms2;
                break;
            case UnitAcceleration.G:
                labelMotionUnit = R.string.unit_acceleration_g;
                break;
            case UnitAcceleration.GAL:
                labelMotionUnit = R.string.unit_acceleration_gal;
                break;
            case UnitAcceleration.CENTIMETERS_PER_SECOND_2:
                labelMotionUnit = R.string.unit_acceleration_cms2;
                break;
            default:
                labelMotionUnit = R.string.unit_acceleration_ms2;
                break;
        }
    }
}
