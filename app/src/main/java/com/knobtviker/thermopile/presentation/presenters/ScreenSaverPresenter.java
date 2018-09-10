package com.knobtviker.thermopile.presentation.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.data.models.presentation.MeasuredData;
import com.knobtviker.thermopile.domain.repositories.AtmosphereRepository;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.ScreenSaverContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Default;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredAirQuality;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredHumidity;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredPressure;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.MeasuredTemperature;
import com.knobtviker.thermopile.presentation.shared.constants.settings.ClockMode;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDay;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitPressure;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;
import com.knobtviker.thermopile.presentation.utils.MathKit;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.MainThreadDisposable;

/**
 * Created by bojan on 15/07/2017.
 */

public class ScreenSaverPresenter extends AbstractPresenter<ScreenSaverContract.View> implements ScreenSaverContract.Presenter {

    @NonNull
    private ZoneId dateTimeZone = DateTimeKit.zoneById(Default.TIMEZONE);

    @ClockMode
    private int formatClock = ClockMode._24H;

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

    @NonNull
    private final AtmosphereRepository atmosphereRepository;

    @NonNull
    private final SettingsRepository settingsRepository;

    @Inject
    public ScreenSaverPresenter(
        @NonNull final ScreenSaverContract.View view,
        @NonNull final AtmosphereRepository atmosphereRepository,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final Schedulers schedulers
    ) {
        super(view, schedulers);
        this.atmosphereRepository = atmosphereRepository;
        this.settingsRepository = settingsRepository;
    }

    @Override
    public void observeDateChanged(@NonNull Context context) {
        compositeDisposable.add(
            Observable.defer(() ->
                Observable.create(emitter -> {
                    final BroadcastReceiver receiver = new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            emitter.onNext(true);
                        }
                    };

                    final IntentFilter filter = new IntentFilter();
                    filter.addAction(Intent.ACTION_DATE_CHANGED);

                    context.registerReceiver(receiver, filter);

                    emitter.setDisposable(new MainThreadDisposable() {
                        @Override
                        protected void onDispose() {
                            context.unregisterReceiver(receiver);

                            dispose();
                        }
                    });
                })
            )
                .subscribe(
                    //                    item -> this.onDateChanged(DateTimeKit.format(ZonedDateTime.now(dateTimeZone), formatDate)),
                    item -> this.onDateChanged(ZonedDateTime.now(dateTimeZone)),
                    this::error
                )
        );
    }

    @Override
    public void observeAtmosphere() {
        setUnitTemperature();
        setUnitHumidity();
        setUnitPressure();
        setUnitMotion();

        settings();
    }

    private void settings() {
        compositeDisposable.add(
            settingsRepository
                .observe()
                .doOnNext(item -> {
                    observeTemperature();
                    observePressure();
                    observeHumidity();
                    observeAirQuality();
                    observeAcceleration();
                })
                .subscribe(
                    this::onSettingsChanged,
                    this::error
                )
        );
    }

    private void observeTemperature() {
        compositeDisposable.add(
            atmosphereRepository
                .observeTemperature()
                .subscribe(
                    this::onTemperatureChanged,
                    this::error
                )
        );
    }

    private void observeHumidity() {
        compositeDisposable.add(
            atmosphereRepository
                .observeHumidity()
                .subscribe(
                    this::onHumidityChanged,
                    this::error
                )
        );
    }

    private void observePressure() {
        compositeDisposable.add(
            atmosphereRepository
                .observePressure()
                .subscribe(
                    this::onPressureChanged,
                    this::error
                )
        );
    }

    private void observeAirQuality() {
        compositeDisposable.add(
            atmosphereRepository
                .observeAirQuality()
                .subscribe(
                    this::onAirQualityChanged,
                    this::error
                )
        );
    }

    private void observeAcceleration() {
        compositeDisposable.add(
            atmosphereRepository
                .observeAcceleration()
                .subscribe(
                    this::onAccelerationChanged,
                    this::error
                )
        );
    }

    private void onDateChanged(@NonNull final ZonedDateTime dateTime) {
        if (formatDate.contains(FormatDay.EEEE)) {
            view.onDateChanged(
                DateTimeKit.format(dateTime, formatDate.replace(FormatDay.EEEE, "").trim()),
                DateTimeKit.format(dateTime, FormatDay.EEEE)
            );
        } else if (formatDate.contains(FormatDay.EE)) {
            view.onDateChanged(
                DateTimeKit.format(dateTime, formatDate.replace(FormatDay.EE, "").trim()),
                DateTimeKit.format(dateTime, FormatDay.EE)
            );
        } else {
            view.onDateChanged(
                DateTimeKit.format(dateTime, formatDate),
                ""
            );
        }
    }

    private void onSettingsChanged(@NonNull final Settings settings) {
        if (dateTimeZone != ZoneId.of(settings.timezone) || !formatDate.equals(settings.formatDate)) {
            dateTimeZone = ZoneId.of(settings.timezone);
            formatDate = settings.formatDate;

            onDateChanged(ZonedDateTime.now(dateTimeZone));
        }

        if (dateTimeZone != ZoneId.of(settings.timezone)
            || formatClock != settings.formatClock
            || !formatTime.equals(settings.formatTime)
        ) {
            dateTimeZone = ZoneId.of(settings.timezone);
            formatClock = settings.formatClock;
            formatTime = settings.formatTime;

            view.onZoneAndFormatChanged(dateTimeZone.getId(), formatClock == ClockMode._24H, formatTime);
        }

        if (unitTemperature != settings.unitTemperature) {
            unitTemperature = settings.unitTemperature;

            setUnitTemperature();
        }

        if (unitPressure != settings.unitPressure) {
            unitPressure = settings.unitPressure;

            setUnitPressure();
        }

        if (unitMotion != settings.unitMotion) {
            unitMotion = settings.unitMotion;

            setUnitMotion();
        }
    }

    private void onTemperatureChanged(final float value) {
        view.onTemperatureChanged(
            MeasuredData.builder()
                .value(value)
                .progress(value / MeasuredTemperature.MAXIMUM)
                .readableValue(String.valueOf(MathKit.round(MathKit.applyTemperatureUnit(unitTemperature, value))))
                .build()
        );
    }

    private void onPressureChanged(final float value) {
        view.onPressureChanged(
            MeasuredData.builder()
                .value(value)
                .progress(value / MeasuredPressure.MAXIMUM)
                .readableValue(String.valueOf(MathKit.round(MathKit.applyPressureUnit(unitPressure, value))))
                .build()
        );
    }

    private void onHumidityChanged(final float value) {
        view.onHumidityChanged(
            MeasuredData.builder()
                .value(value)
                .progress(value / MeasuredHumidity.MAXIMUM)
                .readableValue(String.valueOf(MathKit.round(value)))
                .build()
        );
    }

    private void onAirQualityChanged(final float value) {
        final Pair<String, Integer> pair = MathKit.convertIAQValueToLabelAndColor(value);

        view.onAirQualityChanged(
            MeasuredData.builder()
                .value(value)
                .progress((MeasuredAirQuality.MAXIMUM - value) / MeasuredAirQuality.MAXIMUM)
                .color(pair.second)
                .readableValue(pair.first)
                .build()
        );
    }

    private void onAccelerationChanged(final float[] values) {
        final double ax = values[0];
        final double ay = values[2] + 0.2f;
        final double az = values[1] + SensorManager.GRAVITY_EARTH; //možda minus

        //ax: -0.10000000149011612 ay: -0.20000000298023224 az: -9.800000190734863 + 9.80665F
        //        Timber.i("ax: %s ay: %s az: %s", ax, ay, az);

        final float value = (float) Math.min(Math.sqrt(Math.pow(ax, 2) + Math.pow(ay, 2) + Math.pow(az, 2)), MeasuredAcceleration.MAXIMUM);

        view.onAccelerationChanged(
            MeasuredData.builder()
                .value(value)
                .progress(value / MeasuredAcceleration.MAXIMUM) // 2g in m/s2
                .readableValue(String.valueOf(MathKit.roundToOne(MathKit.applyAccelerationUnit(unitMotion, value))))
                .build()
        );
    }

    private void setUnitTemperature() {
        switch (unitTemperature) {
            case UnitTemperature.CELSIUS:
                view.onTemperatureUnitChanged(R.string.unit_temperature_celsius);
                break;
            case UnitTemperature.FAHRENHEIT:
                view.onTemperatureUnitChanged(R.string.unit_temperature_fahrenheit);
                break;
            case UnitTemperature.KELVIN:
                view.onTemperatureUnitChanged(R.string.unit_temperature_kelvin);
                break;
            default:
                view.onTemperatureUnitChanged(R.string.unit_temperature_celsius);
                break;
        }
    }

    private void setUnitHumidity() {
        view.ontHumidityUnitChanged(R.string.unit_humidity_percent);
    }

    private void setUnitPressure() {
        switch (unitPressure) {
            case UnitPressure.PASCAL:
                view.onPressureUnitChanged(R.string.unit_pressure_pascal);
                break;
            case UnitPressure.BAR:
                view.onPressureUnitChanged(R.string.unit_pressure_bar);
                break;
            case UnitPressure.PSI:
                view.onPressureUnitChanged(R.string.unit_pressure_psi);
                break;
            default:
                view.onPressureUnitChanged(R.string.unit_pressure_pascal);
                break;
        }
    }

    private void setUnitMotion() {
        switch (unitMotion) {
            case UnitAcceleration.METERS_PER_SECOND_2:
                view.onMotionUnitChanged(R.string.unit_acceleration_ms2);
                break;
            case UnitAcceleration.G:
                view.onMotionUnitChanged(R.string.unit_acceleration_g);
                break;
            case UnitAcceleration.GAL:
                view.onMotionUnitChanged(R.string.unit_acceleration_gal);
                break;
            case UnitAcceleration.CENTIMETERS_PER_SECOND_2:
                view.onMotionUnitChanged(R.string.unit_acceleration_cms2);
                break;
            default:
                view.onMotionUnitChanged(R.string.unit_acceleration_ms2);
                break;
        }
    }
}
