package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.contracts.UnitsContract;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitPressure;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;

import java.util.Objects;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class UnitsFragment extends BaseFragment<UnitsContract.Presenter> implements UnitsContract.View {

    @BindView(R.id.scrollview_content)
    public NestedScrollView scrollViewContent;

    @BindView(R.id.progressbar)
    public ContentLoadingProgressBar progressBar;

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
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        scrollViewContent.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);

        Snackbar.make(Objects.requireNonNull(getView()), throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onCelsiusChecked() {
        radioButtonUnitCelsius.setChecked(true);
        radioGroupTemperatureUnit.setEnabled(true);
    }

    @Override
    public void onFahrenheitChecked() {
        radioButtonUnitFarenheit.setChecked(true);
        radioGroupTemperatureUnit.setEnabled(true);
    }

    @Override
    public void onKelvinChecked() {
        radioButtonUnitKelvin.setChecked(true);
        radioGroupTemperatureUnit.setEnabled(true);
    }

    @Override
    public void onPascalChecked() {
        radioButtonUnitPascal.setChecked(true);
        radioGroupPressureUnit.setEnabled(true);
    }

    @Override
    public void onBarChecked() {
        radioButtonUnitBar.setChecked(true);
        radioGroupPressureUnit.setEnabled(true);
    }

    @Override
    public void onPsiChecked() {
        radioButtonUnitPsi.setChecked(true);
        radioGroupPressureUnit.setEnabled(true);
    }

    @Override
    public void onMs2Checked() {
        radioButtonUnitMs2.setChecked(true);
        radioGroupAccelerationUnit.setEnabled(true);
    }

    @Override
    public void onGChecked() {
        radioButtonUnitG.setChecked(true);
        radioGroupAccelerationUnit.setEnabled(true);
    }

    @Override
    public void onCms2Checked() {
        radioButtonUnitCms2.setChecked(true);
        radioGroupAccelerationUnit.setEnabled(true);
    }

    @Override
    public void onGalChecked() {
        radioButtonUnitGal.setChecked(true);
        radioGroupAccelerationUnit.setEnabled(true);
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
                radioGroupTemperatureUnit.setEnabled(false);
                presenter.saveTemperatureUnit(value);
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
                radioGroupPressureUnit.setEnabled(false);
                presenter.savePressureUnit(value);
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
                radioGroupAccelerationUnit.setEnabled(false);
                presenter.saveAccelerationUnit(value);
            }
        });
    }
}
