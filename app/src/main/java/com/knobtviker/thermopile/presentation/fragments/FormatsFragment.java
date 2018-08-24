package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.FormatsContract;
import com.knobtviker.thermopile.presentation.presenters.FormatsPresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatDate;
import com.knobtviker.thermopile.presentation.shared.constants.settings.FormatTime;
import com.knobtviker.thermopile.presentation.views.adapters.FormatAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnItemSelected;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class FormatsFragment extends BaseFragment<FormatsContract.Presenter> implements FormatsContract.View {

    private FormatAdapter spinnerAdapterDate;
    private FormatAdapter spinnerAdapterTime;

    private long settingsId = -1L;

    @FormatDate
    private String formatDate = FormatDate.EEEE_DD_MM_YYYY;

    @FormatTime
    private String formatTime = FormatTime.HH_MM;

    @BindView(R.id.spinner_date_format)
    public Spinner spinnerFormatDate;

    @BindView(R.id.spinner_time_format)
    public Spinner spinnerFormatTime;

    public FormatsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_formats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupSpinnerDate();
        setupSpinnerTime();

        presenter = new FormatsPresenter(this);

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
        this.formatDate = settings.formatDate;
        this.formatTime = settings.formatTime;

        setFormatDate();
        setFormatTime();
    }

    @OnItemSelected(value = {R.id.spinner_date_format, R.id.spinner_time_format}, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onItemSelected(@NonNull final AdapterView<?> adapterView, @NonNull final View view, final int position, final long id) {
        switch (adapterView.getId()) {
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
