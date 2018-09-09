package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.UnitsContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitPressure;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;

import javax.inject.Inject;

import io.reactivex.internal.functions.Functions;

/**
 * Created by bojan on 15/07/2017.
 */

public class UnitsPresenter extends AbstractPresenter<UnitsContract.View> implements UnitsContract.Presenter {

    private long settingsId = -1L;

    @UnitTemperature
    private int unitTemperature = UnitTemperature.CELSIUS;

    @UnitPressure
    private int unitPressure = UnitPressure.PASCAL;

    @UnitAcceleration
    private int unitAcceleration = UnitAcceleration.METERS_PER_SECOND_2;

    @NonNull
    private final SettingsRepository settingsRepository;

    @Inject
    public UnitsPresenter(
        @NonNull final UnitsContract.View view,
        @NonNull final SettingsRepository settingsRepository,
        @NonNull final Schedulers schedulers
    ) {
        super(view, schedulers);
        this.settingsRepository = settingsRepository;
    }

    @Override
    public void load() {
        compositeDisposable.add(
            settingsRepository
                .load()
                .doOnSubscribe(consumer -> {
                    subscribed();
                    setUnitTemperature();
                    setUnitPressure();
                    setUnitAcceleration();
                })
                .doOnTerminate(this::terminated)
                .subscribe(
                    this::onLoad,
                    this::error
                )
        );
    }

    private void onLoad(@NonNull final Settings settings) {
        settingsId = settings.id;

        if (unitTemperature != settings.unitTemperature) {
            unitTemperature = settings.unitTemperature;

            setUnitTemperature();
        }
        if (unitPressure != settings.unitPressure) {
            unitPressure = settings.unitPressure;

            setUnitPressure();
        }
        if (unitAcceleration != settings.unitMotion) {
            unitAcceleration = settings.unitMotion;

            setUnitAcceleration();
        }
    }

    @Override
    public void saveTemperatureUnit(int unit) {
        compositeDisposable.add(
            settingsRepository
                .saveTemperatureUnit(settingsId, unit)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void savePressureUnit(int unit) {
        compositeDisposable.add(
            settingsRepository
                .savePressureUnit(settingsId, unit)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void saveAccelerationUnit(int unit) {
        compositeDisposable.add(
            settingsRepository
                .saveAccelerationUnit(settingsId, unit)
                .doOnSubscribe(consumer -> subscribed())
                .doOnTerminate(this::terminated)
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    private void setUnitTemperature() {
        switch (unitTemperature) {
            case UnitTemperature.CELSIUS:
                view.onCelsiusChecked();
                break;
            case UnitTemperature.FAHRENHEIT:
                view.onFahrenheitChecked();
                break;
            case UnitTemperature.KELVIN:
                view.onKelvinChecked();
                break;
        }
    }

    private void setUnitPressure() {
        switch (unitPressure) {
            case UnitPressure.PASCAL:
                view.onPascalChecked();
                break;
            case UnitPressure.BAR:
                view.onBarChecked();
                break;
            case UnitPressure.PSI:
                view.onPsiChecked();
                break;
        }
    }

    private void setUnitAcceleration() {
        switch (unitAcceleration) {
            case UnitAcceleration.METERS_PER_SECOND_2:
                view.onMs2Checked();
                break;
            case UnitAcceleration.G:
                view.onGChecked();
                break;
            case UnitAcceleration.CENTIMETERS_PER_SECOND_2:
                view.onCms2Checked();
                break;
            case UnitAcceleration.GAL:
                view.onGalChecked();
                break;
        }
    }
}
