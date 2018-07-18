package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.UnitsContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.UnitsPresenter;
import com.knobtviker.thermopile.presentation.utils.constants.UnitAcceleration;
import com.knobtviker.thermopile.presentation.utils.constants.UnitPressure;
import com.knobtviker.thermopile.presentation.utils.constants.UnitTemperature;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class UnitsFragment extends BaseFragment<UnitsContract.Presenter> implements UnitsContract.View {
    public static final String TAG = UnitsFragment.class.getSimpleName();

    private long settingsId = -1L;

    @UnitTemperature
    private int unitTemperature = UnitTemperature.CELSIUS;

    @UnitPressure
    private int unitPressure = UnitPressure.PASCAL;

    @UnitAcceleration
    private int unitAcceleration = UnitAcceleration.METERS_PER_SECOND_2;

    @BindView(R.id.radiogroup_temperature_unit)
    public RadioGroup radioGroupTemperatureUnit;

    @BindView(R.id.unit_celsius)
    public RadioButton radioButtonUnitCelsius;

    @BindView(R.id.unit_farenheit)
    public RadioButton radioButtonUnitFarenheit;

    @BindView(R.id.unit_kelvin)
    public RadioButton radioButtonUnitKelvin;

    @BindView(R.id.radiogroup_pressure_unit)
    public RadioGroup radioGroupPressureUnit;

    @BindView(R.id.unit_pascal)
    public RadioButton radioButtonUnitPascal;

    @BindView(R.id.unit_bar)
    public RadioButton radioButtonUnitBar;

    @BindView(R.id.unit_psi)
    public RadioButton radioButtonUnitPsi;

    @BindView(R.id.radiogroup_acceleration_unit)
    public RadioGroup radioGroupAccelerationUnit;

    @BindView(R.id.unit_ms2)
    public RadioButton radioButtonUnitMs2;

    @BindView(R.id.unit_g)
    public RadioButton radioButtonUnitG;

    @BindView(R.id.unit_cms2)
    public RadioButton radioButtonUnitCms2;

    @BindView(R.id.unit_gal)
    public RadioButton radioButtonUnitGal;

    public static UnitsFragment newInstance() {
        return new UnitsFragment();
    }

    public UnitsFragment() {
        presenter = new UnitsPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_units, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRadioGroupTemperatureUnit();
        setupRadioGroupPressureUnit();
        setupRadioGroupAccelerationUnit();
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);
    }

    public void onLoad(@NonNull Settings settings) {
        this.settingsId = settings.id;
        this.unitTemperature = settings.unitTemperature;
        this.unitPressure = settings.unitPressure;
        this.unitAcceleration = settings.unitMotion;

        setUnitTemperature();
        setUnitPressure();
        setUnitAcceleration();
    }

    private void setupRadioGroupTemperatureUnit() {
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

    private void setupRadioGroupPressureUnit() {
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

    private void setupRadioGroupAccelerationUnit() {
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
