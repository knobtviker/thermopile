package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultMotion;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultPressure;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultTemperature;
import com.knobtviker.thermopile.presentation.contracts.UnitsContract;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitPressure;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class UnitsFragment extends BaseFragment<UnitsContract.Presenter> implements UnitsContract.View {

    @Inject
    long settingsId;

    @Inject
    @DefaultTemperature
    @UnitTemperature
    int unitTemperature;

    @Inject
    @DefaultPressure
    @UnitPressure
    int unitPressure;

    @Inject
    @DefaultMotion
    @UnitAcceleration
    int unitAcceleration;

    @BindView(R.id.radiogroup_temperature_unit)
    public ChipGroup radioGroupTemperatureUnit;

    @BindView(R.id.unit_celsius)
    public Chip radioButtonUnitCelsius;

    @BindView(R.id.unit_farenheit)
    public Chip radioButtonUnitFarenheit;

    @BindView(R.id.unit_kelvin)
    public Chip radioButtonUnitKelvin;

    @BindView(R.id.radiogroup_pressure_unit)
    public ChipGroup radioGroupPressureUnit;

    @BindView(R.id.unit_pascal)
    public Chip radioButtonUnitPascal;

    @BindView(R.id.unit_bar)
    public Chip radioButtonUnitBar;

    @BindView(R.id.unit_psi)
    public Chip radioButtonUnitPsi;

    @BindView(R.id.radiogroup_acceleration_unit)
    public ChipGroup radioGroupAccelerationUnit;

    @BindView(R.id.unit_ms2)
    public Chip radioButtonUnitMs2;

    @BindView(R.id.unit_g)
    public Chip radioButtonUnitG;

    @BindView(R.id.unit_cms2)
    public Chip radioButtonUnitCms2;

    @BindView(R.id.unit_gal)
    public Chip radioButtonUnitGal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_units, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupGroupTemperatureUnit();
        setupGroupPressureUnit();
        setupGroupAccelerationUnit();
    }

    @Override
    protected void load() {
        presenter.load();
    }

    @Override
    public void showLoading(boolean isLoading) {
        //TODO: Do some loading if needed
    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);

        Snackbar.make(Objects.requireNonNull(getView()), throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onLoad(@NonNull Settings settings) {
        this.settingsId = settings.id;
        this.unitTemperature = settings.unitTemperature;
        this.unitPressure = settings.unitPressure;
        this.unitAcceleration = settings.unitMotion;

        setUnitTemperature();
        setUnitPressure();
        setUnitAcceleration();
    }

    private void setupGroupTemperatureUnit() {
        radioGroupTemperatureUnit.setEnabled(false);
        radioGroupTemperatureUnit.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            int value;
            switch (checkedId) {
                case R.id.unit_celsius:
                    value = UnitTemperature.CELSIUS;
                    break;
                case R.id.unit_farenheit:
                    value = UnitTemperature.FAHRENHEIT;
                    break;
                case R.id.unit_kelvin:
                    value = UnitTemperature.KELVIN;
                    break;
                default:
                    value = UnitTemperature.CELSIUS;
                    break;
            }
            if (radioGroupTemperatureUnit.isEnabled()) {
                this.unitTemperature = value;
                presenter.saveTemperatureUnit(settingsId, value);
            }
        });
    }

    private void setupGroupPressureUnit() {
        radioGroupPressureUnit.setEnabled(false);
        radioGroupPressureUnit.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            int value;
            switch (checkedId) {
                case R.id.unit_pascal:
                    value = UnitPressure.PASCAL;
                    break;
                case R.id.unit_bar:
                    value = UnitPressure.BAR;
                    break;
                case R.id.unit_psi:
                    value = UnitPressure.PSI;
                    break;
                default:
                    value = UnitPressure.PASCAL;
                    break;
            }
            if (radioGroupPressureUnit.isEnabled()) {
                this.unitPressure = value;
                presenter.savePressureUnit(settingsId, value);
            }
        });
    }

    private void setupGroupAccelerationUnit() {
        radioGroupAccelerationUnit.setEnabled(false);
        radioGroupAccelerationUnit.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            int value;
            switch (checkedId) {
                case R.id.unit_ms2:
                    value = UnitAcceleration.METERS_PER_SECOND_2;
                    break;
                case R.id.unit_g:
                    value = UnitAcceleration.G;
                    break;
                case R.id.unit_gal:
                    value = UnitAcceleration.GAL;
                    break;
                case R.id.unit_cms2:
                    value = UnitAcceleration.CENTIMETERS_PER_SECOND_2;
                    break;
                default:
                    value = UnitAcceleration.METERS_PER_SECOND_2;
                    break;
            }
            if (radioGroupAccelerationUnit.isEnabled()) {
                this.unitAcceleration = value;
                presenter.saveAccelerationUnit(settingsId, value);
            }
        });
    }

    private void setUnitTemperature() {
        switch (unitTemperature) {
            case UnitTemperature.CELSIUS:
                radioButtonUnitCelsius.setChecked(true);
                break;
            case UnitTemperature.FAHRENHEIT:
                radioButtonUnitFarenheit.setChecked(true);
                break;
            case UnitTemperature.KELVIN:
                radioButtonUnitKelvin.setChecked(true);
                break;
        }

        radioGroupTemperatureUnit.setEnabled(true);
    }

    private void setUnitPressure() {
        switch (unitPressure) {
            case UnitPressure.PASCAL:
                radioButtonUnitPascal.setChecked(true);
                break;
            case UnitPressure.BAR:
                radioButtonUnitBar.setChecked(true);
                break;
            case UnitPressure.PSI:
                radioButtonUnitPsi.setChecked(true);
                break;
        }

        radioGroupPressureUnit.setEnabled(true);
    }

    private void setUnitAcceleration() {
        switch (unitAcceleration) {
            case UnitAcceleration.METERS_PER_SECOND_2:
                radioButtonUnitMs2.setChecked(true);
                break;
            case UnitAcceleration.G:
                radioButtonUnitG.setChecked(true);
                break;
            case UnitAcceleration.CENTIMETERS_PER_SECOND_2:
                radioButtonUnitCms2.setChecked(true);
                break;
            case UnitAcceleration.GAL:
                radioButtonUnitGal.setChecked(true);
                break;
        }

        radioGroupAccelerationUnit.setEnabled(true);
    }
}
