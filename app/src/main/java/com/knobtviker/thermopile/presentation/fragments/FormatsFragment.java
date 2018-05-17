package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.FormatsContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.FormatsPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class FormatsFragment extends BaseFragment<FormatsContract.Presenter> implements FormatsContract.View {
    public static final String TAG = FormatsFragment.class.getSimpleName();

    private ArrayAdapter<String> spinnerAdapterDate;
    private ArrayAdapter<String> spinnerAdapterTime;

    private long settingsId = -1L;
    private String formatDate = Constants.DEFAULT_FORMAT_DATE;
    private String formatTime = Constants.FORMAT_TIME_LONG_24H;

    @BindView(R.id.spinner_date_format)
    public Spinner spinnerFormatDate;

    @BindView(R.id.spinner_time_format)
    public Spinner spinnerFormatTime;

    public static FormatsFragment newInstance() {
        return new FormatsFragment();
    }

    public FormatsFragment() {
        presenter = new FormatsPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_formats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupSpinnerDate();
        setupSpinnerTime();
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
        this.formatDate = settings.formatDate;
        this.formatTime = settings.formatTime;

        setFormatDate();
        setFormatTime();
    }

    private void setupSpinnerDate() {
        spinnerFormatDate.setEnabled(false);
        spinnerFormatDate.setPrompt("Date format");
        spinnerFormatDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerFormatDate.isEnabled() && spinnerAdapterDate != null && !TextUtils.isEmpty(spinnerAdapterDate.getItem(i))) {
                    formatDate = spinnerAdapterDate.getItem(i);
                    if (!TextUtils.isEmpty(formatDate)) {
                        presenter.saveFormatDate(settingsId, formatDate);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //TODO: Move this somewhere that is easy to maintain and not hardcoded
        final List<String> formats = Arrays.asList(
            "EEEE dd.MM.yyyy.",
            "EE dd.MM.yyyy.",
            "dd.MM.yyyy.",
            "EEEE MM/dd/yyyy",
            "EE MM/dd/yyyy",
            "MM/dd/yyyy"
        );
        spinnerAdapterDate = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, formats);
        spinnerAdapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFormatDate.setAdapter(spinnerAdapterDate);
    }

    private void setupSpinnerTime() {
        spinnerFormatTime.setEnabled(false);
        spinnerFormatTime.setPrompt("Time format");
        spinnerFormatTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerFormatTime.isEnabled() && spinnerAdapterTime != null && !TextUtils.isEmpty(spinnerAdapterTime.getItem(i))) {
                    formatTime = spinnerAdapterTime.getItem(i);
                    presenter.saveFormatTime(settingsId, spinnerAdapterTime.getItem(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final List<String> formats = Arrays.asList(Constants.FORMAT_TIME_LONG_24H, Constants.FORMAT_TIME_SHORT_24H);
        spinnerAdapterTime = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, formats);
        spinnerAdapterTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
