package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.SettingsContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.SettingsPresenter;
import com.knobtviker.thermopile.presentation.utils.Constants;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * Created by bojan on 15/06/2017.
 */

public class SettingsFormatFragment extends BaseFragment<SettingsContract.Presenter> implements SettingsContract.View {
    public static final String TAG = SettingsFormatFragment.class.getSimpleName();

    private ArrayAdapter<String> spinnerAdapterDate;
    private ArrayAdapter<String> spinnerAdapterTime;

    private long settingsId = -1L;

    @BindView(R.id.spinner_date_format)
    public Spinner spinnerFormatDate;

    @BindView(R.id.spinner_time_format)
    public Spinner spinnerFormatTime;

    public static Fragment newInstance() {
        return new SettingsFormatFragment();
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

        final View view = inflater.inflate(R.layout.fragment_settings_format, container, false);

        bind(this, view);

        setupSpinnerDate();
        setupSpinnerTime();

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

        final String formatDate = settings.formatDate();
        for (int i = 0; i < spinnerAdapterDate.getCount(); i++) {
            if (spinnerAdapterDate.getItem(i).equalsIgnoreCase(formatDate)) {
                spinnerFormatDate.setSelection(i);
                break;
            }
        }

        final String formatTime = settings.formatTime();
        for (int i = 0; i < spinnerAdapterTime.getCount(); i++) {
            if (spinnerAdapterTime.getItem(i).equalsIgnoreCase(formatTime)) {
                spinnerFormatTime.setSelection(i);
                break;
            }
        }

        spinnerFormatDate.setEnabled(true);
        spinnerFormatTime.setEnabled(true);
    }

    private void setupSpinnerDate() {
        spinnerFormatDate.setEnabled(false);
        spinnerFormatDate.setPrompt("Date format");
        spinnerFormatDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerAdapterDate != null && !TextUtils.isEmpty(spinnerAdapterDate.getItem(i))) {
                    presenter.saveFormatDate(settingsId, spinnerAdapterDate.getItem(i));
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
                if (spinnerAdapterTime != null && !TextUtils.isEmpty(spinnerAdapterTime.getItem(i))) {
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
}
