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
import com.knobtviker.thermopile.presentation.utils.Constants;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class UnitsFragment extends BaseFragment<UnitsContract.Presenter> implements UnitsContract.View {
    public static final String TAG = UnitsFragment.class.getSimpleName();

    private long settingsId = -1L;
    private int unitTemperature = Constants.UNIT_TEMPERATURE_CELSIUS;
    private int unitPressure = Constants.UNIT_PRESSURE_PASCAL;

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

    public static UnitsFragment newInstance() {
        return new UnitsFragment();
    }

    public UnitsFragment() {
        presenter = new UnitsPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_units, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRadioGroupTemperatureUnit();
        setupRadioGroupPressureUnit();
    }

    @Override
    public void onResume() {
        setUnitTemperature();
        setUnitPressure();

        super.onResume();
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
    }

    private void setupRadioGroupTemperatureUnit() {
        radioGroupTemperatureUnit.setEnabled(false);
        radioGroupTemperatureUnit.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            int value;
            switch (checkedId) {
                case R.id.unit_celsius:
                    value = Constants.UNIT_TEMPERATURE_CELSIUS;
                    break;
                case R.id.unit_farenheit:
                    value = Constants.UNIT_TEMPERATURE_FAHRENHEIT;
                    break;
                case R.id.unit_kelvin:
                    value = Constants.UNIT_TEMPERATURE_KELVIN;
                    break;
                default:
                    value = Constants.UNIT_TEMPERATURE_CELSIUS;
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
                    value = Constants.UNIT_PRESSURE_PASCAL;
                    break;
                case R.id.unit_bar:
                    value = Constants.UNIT_PRESSURE_BAR;
                    break;
                case R.id.unit_psi:
                    value = Constants.UNIT_PRESSURE_PSI;
                    break;
                default:
                    value = Constants.UNIT_PRESSURE_PASCAL;
                    break;
            }
            if (radioGroupPressureUnit.isEnabled()) {
                this.unitPressure = value;
                presenter.savePressureUnit(settingsId, value);
            }
        });
    }

    private void setUnitTemperature() {
        switch (unitTemperature) {
            case Constants.UNIT_TEMPERATURE_CELSIUS:
                radioButtonUnitCelsius.setChecked(true);
                break;
            case Constants.UNIT_TEMPERATURE_FAHRENHEIT:
                radioButtonUnitFarenheit.setChecked(true);
                break;
            case Constants.UNIT_TEMPERATURE_KELVIN:
                radioButtonUnitKelvin.setChecked(true);
                break;
        }

        radioGroupTemperatureUnit.setEnabled(true);
    }

    private void setUnitPressure() {
        switch (unitPressure) {
            case Constants.UNIT_PRESSURE_PASCAL:
                radioButtonUnitPascal.setChecked(true);
                break;
            case Constants.UNIT_PRESSURE_BAR:
                radioButtonUnitBar.setChecked(true);
                break;
            case Constants.UNIT_PRESSURE_PSI:
                radioButtonUnitPsi.setChecked(true);
                break;
        }

        radioGroupPressureUnit.setEnabled(true);
    }
}
