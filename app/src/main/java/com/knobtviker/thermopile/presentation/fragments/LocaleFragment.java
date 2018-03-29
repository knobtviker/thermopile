package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.LocaleContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.LocalePresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;

import org.joda.time.DateTimeZone;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by bojan on 15/06/2017.
 */

public class LocaleFragment extends BaseFragment<LocaleContract.Presenter> implements LocaleContract.View {
    public static final String TAG = LocaleFragment.class.getSimpleName();

    private long settingsId = -1L;
    private String timezone = Constants.DEFAULT_TIMEZONE;
    private int clockMode  = Constants.CLOCK_MODE_24H;

    private ArrayAdapter<String> spinnerAdapter;

    @BindView(R.id.spinner_timezone)
    public Spinner spinnerTimezone;

    @BindView(R.id.radiogroup_clock_mode)
    public RadioGroup radioGroupClockMode;

    @BindView(R.id.mode_12h)
    public RadioButton radioButton12h;

    @BindView(R.id.mode_24h)
    public RadioButton radioButton24h;

    public static LocaleFragment newInstance() {
        return new LocaleFragment();
    }

    public LocaleFragment() {
        presenter = new LocalePresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_locale, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupSpinnerTimezone();
        setupRadioGroupClockMode();
    }

    @Override
    public void onResume() {
        setTimezone();
        setClockMode();

        super.onResume();
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    public void onLoad(@NonNull Settings settings) {
        this.settingsId = settings.id();
        this.timezone = settings.timezone();
        this.clockMode = settings.formatClock();
    }

    private void setupSpinnerTimezone() {
        spinnerTimezone.setEnabled(false);
        spinnerTimezone.setPrompt("Timezone");
        spinnerTimezone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerTimezone.isEnabled() && spinnerAdapter != null && !TextUtils.isEmpty(spinnerAdapter.getItem(i))) {
                    timezone = spinnerAdapter.getItem(i);
                    presenter.saveTimezone(settingsId, spinnerAdapter.getItem(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, new ArrayList<>(DateTimeZone.getAvailableIDs()));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTimezone.setAdapter(spinnerAdapter);
    }

    private void setupRadioGroupClockMode() {
        radioGroupClockMode.setEnabled(false);
        radioGroupClockMode.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            int value;
            switch (checkedId) {
                case R.id.mode_12h:
                    value = Constants.CLOCK_MODE_12H;
                    break;
                case R.id.mode_24h:
                    value = Constants.CLOCK_MODE_24H;
                    break;
                default:
                    value = Constants.CLOCK_MODE_24H;
                    break;
            }
            if (radioGroupClockMode.isEnabled()) {
                this.clockMode = value;
                presenter.saveClockMode(settingsId, value);
            }
        });
    }

    private void setTimezone() {
        for (int i = 0; i<spinnerAdapter.getCount(); i++) {
            if (spinnerAdapter.getItem(i).equalsIgnoreCase(timezone)) {
                spinnerTimezone.setSelection(i);
                break;
            }
        }

        spinnerTimezone.setEnabled(true);
    }

    private void setClockMode() {
        switch (clockMode) {
            case Constants.CLOCK_MODE_12H:
                radioButton12h.setChecked(true);
                break;
            case Constants.CLOCK_MODE_24H:
                radioButton24h.setChecked(true);
                break;
        }

        radioGroupClockMode.setEnabled(true);
    }
}
