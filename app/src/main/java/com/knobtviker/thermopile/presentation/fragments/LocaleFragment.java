package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultClockMode;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatDate;
import com.knobtviker.thermopile.di.qualifiers.presentation.defaults.DefaultFormatTime;
import com.knobtviker.thermopile.presentation.contracts.LocaleContract;
import com.knobtviker.thermopile.presentation.presenters.LocalePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.shared.constants.settings.ClockMode;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;
import com.knobtviker.thermopile.presentation.views.adapters.FormatAdapter;
import com.knobtviker.thermopile.presentation.views.adapters.TimezoneAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnItemSelected;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class LocaleFragment extends BaseFragment<LocaleContract.Presenter> implements LocaleContract.View {

    private long settingsId = -1L;

    @Nullable
    private TimezoneAdapter spinnerAdapter;

    private FormatAdapter spinnerAdapterDate;
    private FormatAdapter spinnerAdapterTime;

    @Inject
    String timezone;

    @Inject
    @DefaultClockMode
    @ClockMode
    int formatClock;

    @Inject
    @DefaultFormatDate
    @FormatDate
    String formatDate;

    @Inject
    @DefaultFormatTime
    @FormatTime
    String formatTime;

    @BindView(R.id.spinner_timezone)
    public Spinner spinnerTimezone;

    @BindView(R.id.radiogroup_clock_mode)
    public ChipGroup radioGroupClockMode;

    @BindView(R.id.mode_12h)
    public Chip radioButton12h;

    @BindView(R.id.mode_24h)
    public Chip radioButton24h;

    @BindView(R.id.spinner_date_format)
    public Spinner spinnerFormatDate;

    @BindView(R.id.spinner_time_format)
    public Spinner spinnerFormatTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_locale, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupSpinnerTimezone();
        setupGroupClockMode();
        setupSpinnerDate();
        setupSpinnerTime();

        presenter = new LocalePresenter(this);

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
        this.timezone = settings.timezone;
        this.formatClock = settings.formatClock;
        this.formatDate = settings.formatDate;
        this.formatTime = settings.formatTime;

        setTimezone();
        setClockMode();
        setFormatDate();
        setFormatTime();
    }

    @OnItemSelected(value = {R.id.spinner_timezone}, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onItemSelected(@NonNull final AdapterView<?> adapterView, @NonNull final View view, final int position, final long id) {
        switch (adapterView.getId()) {
            case R.id.spinner_timezone:
                if (spinnerAdapter != null) {
                    if (spinnerTimezone.isEnabled() && !TextUtils.isEmpty(spinnerAdapter.getItem(position))) {
                        timezone = spinnerAdapter.getItem(position);
                        presenter.saveTimezone(settingsId, timezone);
                    }
                }
                break;
            case R.id.spinner_date_format:
                if (spinnerFormatDate.isEnabled()
                    && spinnerAdapterDate != null
                    && !TextUtils.isEmpty(spinnerAdapterDate.getItem(position))) {
                    formatDate = spinnerAdapterDate.getItem(position);
                    if (!TextUtils.isEmpty(formatDate)) {
                        presenter.saveFormatDate(settingsId, formatDate);
                    }
                }
                break;
            case R.id.spinner_time_format:
                if (spinnerFormatTime.isEnabled()
                    && spinnerAdapterTime != null
                    && !TextUtils.isEmpty(spinnerAdapterTime.getItem(position))) {
                    formatTime = spinnerAdapterTime.getItem(position);
                    presenter.saveFormatTime(settingsId, formatTime);
                }
                break;
        }
    }

    private void setupSpinnerTimezone() {
        spinnerTimezone.setEnabled(false);
        spinnerTimezone.setPrompt("Timezone");

        spinnerAdapter = new TimezoneAdapter(requireContext(), DateTimeKit.zones());
        spinnerTimezone.setAdapter(spinnerAdapter);
    }

    private void setupGroupClockMode() {
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
                this.formatClock = value;
                presenter.saveClockMode(settingsId, value);
            }
        });
    }

    private void setupSpinnerDate() {
        spinnerFormatDate.setEnabled(false);
        spinnerFormatDate.setPrompt("Date format");

        final List<String> formats = Arrays.asList(
            FormatDate.EEEE_DD_MM_YYYY,
            FormatDate.EE_DD_MM_YYYY,
            FormatDate.DD_MM_YYYY,
            FormatDate.EEEE_MM_DD_YYYY,
            FormatDate.EE_MM_DD_YYYY,
            FormatDate.MM_DD_YYYY
        );
        spinnerAdapterDate = new FormatAdapter(spinnerFormatDate.getContext(), formats);
        spinnerFormatDate.setAdapter(spinnerAdapterDate);
    }

    private void setupSpinnerTime() {
        spinnerFormatTime.setEnabled(false);
        spinnerFormatTime.setPrompt("Time format");

        final List<String> formats = Arrays.asList(
            FormatTime.HH_MM,
            FormatTime.H_M,
            FormatTime.KK_MM_A,
            FormatTime.K_M_A
        );

        spinnerAdapterTime = new FormatAdapter(requireContext(), formats);
        spinnerFormatTime.setAdapter(spinnerAdapterTime);
    }

    private void setTimezone() {
        if (spinnerAdapter != null) {
            for (int i = 0; i<spinnerAdapter.getCount(); i++) {
                if (spinnerAdapter.getItem(i).equalsIgnoreCase(timezone)) {
                    spinnerTimezone.setSelection(i);
                    break;
                }
            }
        }

        spinnerTimezone.setEnabled(true);
    }

    private void setClockMode() {
        switch (formatClock) {
            case ClockMode._12H:
                radioButton12h.setChecked(true);
                break;
            case ClockMode._24H:
                radioButton24h.setChecked(true);
                break;
        }

        radioGroupClockMode.setEnabled(true);
    }


    private void setFormatDate() {
        for (int i = 0; i < spinnerAdapterDate.getCount(); i++) {
            if (spinnerAdapterDate.getItem(i).equalsIgnoreCase(formatDate)) {
                spinnerFormatDate.setSelection(i);
                break;
            }
        }

        spinnerFormatDate.setEnabled(true);
    }

    private void setFormatTime() {
        for (int i = 0; i < spinnerAdapterTime.getCount(); i++) {
            if (spinnerAdapterTime.getItem(i).equalsIgnoreCase(formatTime)) {
                spinnerFormatTime.setSelection(i);
                break;
            }
        }

        spinnerFormatTime.setEnabled(true);
    }
}
