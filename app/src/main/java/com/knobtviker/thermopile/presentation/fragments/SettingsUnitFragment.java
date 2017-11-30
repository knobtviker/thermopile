package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.SettingsContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.SettingsPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;

import butterknife.BindView;

/**
 * Created by bojan on 15/06/2017.
 */

public class SettingsUnitFragment extends BaseFragment<SettingsContract.Presenter> implements SettingsContract.View {
    public static final String TAG = SettingsUnitFragment.class.getSimpleName();

    private long settingsId = -1L;

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

    public static Fragment newInstance() {
        return new SettingsUnitFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(false);

        presenter = new SettingsPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        
        final View view = inflater.inflate(R.layout.fragment_settings_unit, container, false);

        bind(this, view);

        setupRadioGroupTemperatureUnit();
        setupRadioGroupPressureUnit();

        presenter.load(realm);

        return view;
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    @Override
    public void onLoad(@NonNull Settings settings) {
        this.settingsId = settings.id();

        switch (settings.unitTemperature()) {
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

        switch (settings.unitPressure()) {
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

        radioGroupTemperatureUnit.setEnabled(true);
        radioGroupPressureUnit.setEnabled(true);
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
            presenter.saveTemperatureUnit(settingsId, value);
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
            presenter.savePressureUnit(settingsId, value);
        });
    }
}
