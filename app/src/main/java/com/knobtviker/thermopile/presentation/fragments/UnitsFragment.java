package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.UnitsContract;
import com.knobtviker.thermopile.presentation.presenters.UnitsPresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitAcceleration;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitPressure;
import com.knobtviker.thermopile.presentation.shared.constants.settings.UnitTemperature;
import com.knobtviker.thermopile.presentation.views.MaterialToggleButton;
import com.knobtviker.thermopile.presentation.views.MaterialToggleGroup;

import java.util.Objects;

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
    public MaterialToggleGroup radioGroupTemperatureUnit;

    @BindView(R.id.unit_celsius)
    public MaterialToggleButton radioButtonUnitCelsius;

    @BindView(R.id.unit_farenheit)
    public MaterialToggleButton radioButtonUnitFarenheit;

    @BindView(R.id.unit_kelvin)
    public MaterialToggleButton radioButtonUnitKelvin;

    @BindView(R.id.radiogroup_pressure_unit)
    public MaterialToggleGroup radioGroupPressureUnit;

    @BindView(R.id.unit_pascal)
    public MaterialToggleButton radioButtonUnitPascal;

    @BindView(R.id.unit_bar)
    public MaterialToggleButton radioButtonUnitBar;

    @BindView(R.id.unit_psi)
    public MaterialToggleButton radioButtonUnitPsi;

    @BindView(R.id.radiogroup_acceleration_unit)
    public MaterialToggleGroup radioGroupAccelerationUnit;

    @BindView(R.id.unit_ms2)
    public MaterialToggleButton radioButtonUnitMs2;

    @BindView(R.id.unit_g)
    public MaterialToggleButton radioButtonUnitG;

    @BindView(R.id.unit_cms2)
    public MaterialToggleButton radioButtonUnitCms2;

    @BindView(R.id.unit_gal)
    public MaterialToggleButton radioButtonUnitGal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_units, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupMaterialToggleGroupTemperatureUnit();
        setupMaterialToggleGroupPressureUnit();
        setupMaterialToggleGroupAccelerationUnit();

        presenter = new UnitsPresenter(this);

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

    private void setupMaterialToggleGroupTemperatureUnit() {
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

    private void setupMaterialToggleGroupPressureUnit() {
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

    private void setupMaterialToggleGroupAccelerationUnit() {
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
