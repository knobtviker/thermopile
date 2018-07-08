package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.LocaleContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.LocalePresenter;
import com.knobtviker.thermopile.presentation.utils.constants.ClockMode;
import com.knobtviker.thermopile.presentation.utils.constants.Default;
import com.knobtviker.thermopile.presentation.views.adapters.TimezoneAdapter;

import org.joda.time.DateTimeZone;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnItemSelected;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class LocaleFragment extends BaseFragment<LocaleContract.Presenter> implements LocaleContract.View {
    public static final String TAG = LocaleFragment.class.getSimpleName();

    private long settingsId = -1L;

    private String timezone = Default.TIMEZONE;

    @ClockMode
    private int clockMode = ClockMode._24H;

    private TimezoneAdapter spinnerAdapter;

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
        return inflater.inflate(R.layout.fragment_locale, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupSpinnerTimezone();
        setupRadioGroupClockMode();
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);
    }

    @OnItemSelected(value = {R.id.spinner_timezone}, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onItemSelected(@NonNull final AdapterView<?> adapterView, @NonNull final View view, final int position, final long id) {
        switch (adapterView.getId()) {
            case R.id.spinner_timezone:
                if (spinnerTimezone.isEnabled() && spinnerAdapter != null && !TextUtils.isEmpty(spinnerAdapter.getItem(position))) {
                    timezone = spinnerAdapter.getItem(position);
                    presenter.saveTimezone(settingsId, timezone);
                }
                break;
        }
    }

    public void onLoad(@NonNull Settings settings) {
        this.settingsId = settings.id;
        this.timezone = settings.timezone;
        this.clockMode = settings.formatClock;

        setTimezone();
        setClockMode();
    }

    private void setupSpinnerTimezone() {
        spinnerTimezone.setEnabled(false);
        spinnerTimezone.setPrompt("Timezone");

        spinnerAdapter = new TimezoneAdapter(requireContext(), new ArrayList<>(DateTimeZone.getAvailableIDs()));
        spinnerTimezone.setAdapter(spinnerAdapter);
    }

    private void setupRadioGroupClockMode() {
        radioGroupClockMode.setEnabled(false);
        radioGroupClockMode.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            int value;
            switch (checkedId) {
                case R.id.mode_12h:
                    value = ClockMode._12H;
                    break;
                case R.id.mode_24h:
                    value = ClockMode._24H;
                    break;
                default:
                    value = ClockMode._24H;
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
            case ClockMode._12H:
                radioButton12h.setChecked(true);
                break;
            case ClockMode._24H:
                radioButton24h.setChecked(true);
                break;
        }

        radioGroupClockMode.setEnabled(true);
    }
}
