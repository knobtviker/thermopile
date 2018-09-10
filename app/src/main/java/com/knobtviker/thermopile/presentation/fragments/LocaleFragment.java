package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.di.qualifiers.presentation.adapters.FormatDateAdapter;
import com.knobtviker.thermopile.di.qualifiers.presentation.adapters.FormatTimeAdapter;
import com.knobtviker.thermopile.presentation.contracts.LocaleContract;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.shared.constants.settings.ClockMode;
import com.knobtviker.thermopile.presentation.views.adapters.FormatAdapter;
import com.knobtviker.thermopile.presentation.views.adapters.TimezoneAdapter;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnItemSelected;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class LocaleFragment extends BaseFragment<LocaleContract.Presenter> implements LocaleContract.View {

    @Inject
    TimezoneAdapter spinnerAdapterTimezones;

    @Inject
    @FormatDateAdapter
    FormatAdapter spinnerAdapterDate;

    @Inject
    @FormatTimeAdapter
    FormatAdapter spinnerAdapterTime;

    @BindView(R.id.scrollview_content)
    public NestedScrollView scrollViewContent;

    @BindView(R.id.progressbar)
    public ContentLoadingProgressBar progressBar;

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
    }

    @Override
    protected void load() {
        presenter.load(
            spinnerAdapterTimezones.getItems(),
            spinnerAdapterDate.getItems(),
            spinnerAdapterTime.getItems()
        );
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
    public void setTimezone(int index) {
        spinnerTimezone.setSelection(index);
        spinnerTimezone.setEnabled(true);
    }

    @Override
    public void button12hChecked() {
        radioButton12h.setChecked(true);
        radioGroupClockMode.setEnabled(true);
    }

    @Override
    public void button24hChecked() {
        radioButton24h.setChecked(true);
        radioGroupClockMode.setEnabled(true);
    }

    @Override
    public void setDateFormat(int index) {
        spinnerFormatDate.setSelection(index);
        spinnerFormatDate.setEnabled(true);
    }

    @Override
    public void setTimeFormat(int index) {
        spinnerFormatTime.setSelection(index);
        spinnerFormatTime.setEnabled(true);
    }

    @OnItemSelected(value = {R.id.spinner_timezone}, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onItemSelected(@NonNull final AdapterView<?> adapterView, @NonNull final View view, final int position, final long id) {
        switch (adapterView.getId()) {
            case R.id.spinner_timezone:
                if (spinnerTimezone.isEnabled() && !TextUtils.isEmpty(spinnerAdapterTimezones.getItem(position))) {
                    presenter.saveTimezone(spinnerAdapterTimezones.getItem(position));
                }
                break;
            case R.id.spinner_date_format:
                if (spinnerFormatDate.isEnabled() && !TextUtils.isEmpty(spinnerAdapterDate.getItem(position))) {
                    presenter.saveFormatDate(spinnerAdapterDate.getItem(position));
                }
                break;
            case R.id.spinner_time_format:
                if (spinnerFormatTime.isEnabled() && !TextUtils.isEmpty(spinnerAdapterTime.getItem(position))) {
                    presenter.saveFormatTime(spinnerAdapterTime.getItem(position));
                }
                break;
        }
    }

    private void setupSpinnerTimezone() {
        spinnerTimezone.setEnabled(false);
        spinnerTimezone.setAdapter(spinnerAdapterTimezones);
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
                presenter.saveClockMode(value);
            }
        });
    }

    private void setupSpinnerDate() {
        spinnerFormatDate.setEnabled(false);
        spinnerFormatDate.setAdapter(spinnerAdapterDate);
    }

    private void setupSpinnerTime() {
        spinnerFormatTime.setEnabled(false);
        spinnerFormatTime.setAdapter(spinnerAdapterTime);
    }
}
