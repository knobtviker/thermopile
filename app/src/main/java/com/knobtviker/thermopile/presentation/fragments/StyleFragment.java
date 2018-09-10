package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.contracts.StyleContract;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.views.adapters.TimeoutAdapter;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnItemSelected;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class StyleFragment extends BaseFragment<StyleContract.Presenter> implements StyleContract.View {

    @Inject
    TimeoutAdapter spinnerAdapterTimeout;

    @BindView(R.id.scrollview_content)
    public NestedScrollView scrollViewContent;

    @BindView(R.id.progressbar)
    public ContentLoadingProgressBar progressBar;

    @BindView(R.id.radiogroup_theme)
    public ChipGroup radioGroupTheme;

    @BindView(R.id.light)
    public Chip radioButtonLight;

    @BindView(R.id.dark)
    public Chip radioButtonDark;

    @BindView(R.id.automatic)
    public Chip radioButtonAutomatic;

    @BindView(R.id.spinner_timeout)
    public Spinner spinnerTimeout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_style, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupGroupTheme();
        setupSpinnerTimeout();
    }

    @Override
    protected void load() {
        presenter.load(spinnerAdapterTimeout.getItems());
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

    @OnItemSelected(value = {R.id.spinner_timeout}, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onItemSelected(@NonNull final AdapterView<?> adapterView, @NonNull final View view, final int position, final long id) {
        switch (adapterView.getId()) {
            case R.id.spinner_timeout:
                if (spinnerTimeout.isEnabled() && spinnerAdapterTimeout != null && spinnerAdapterTimeout.getItem(position) > 0) {
                    final int screenSaverTimeout = spinnerAdapterTimeout.getItem(position);
                    if (screenSaverTimeout > 0) {
                        presenter.saveScreensaverTimeout(screenSaverTimeout);
                    }
                }
                break;
        }
    }

    private void setupGroupTheme() {
        radioGroupTheme.setEnabled(false);
        radioGroupTheme.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            int value;
            switch (checkedId) {
                case R.id.light:
                    value = AppCompatDelegate.MODE_NIGHT_NO;
                    break;
                case R.id.dark:
                    value = AppCompatDelegate.MODE_NIGHT_YES;
                    break;
                case R.id.automatic:
                    value = AppCompatDelegate.MODE_NIGHT_AUTO;
                    break;
                default:
                    value = AppCompatDelegate.MODE_NIGHT_NO;
                    break;
            }
            if (radioGroupTheme.isEnabled()) {
                presenter.saveTheme(value);
                AppCompatDelegate.setDefaultNightMode(value);
                requireActivity().recreate();
            }
        });
    }

    private void setupSpinnerTimeout() {
        spinnerTimeout.setEnabled(false);
        spinnerTimeout.setAdapter(spinnerAdapterTimeout);
    }

    @Override
    public void setTheme(int theme) {
        switch (theme) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                radioButtonLight.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                radioButtonDark.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_AUTO:
                radioButtonAutomatic.setChecked(true);
                break;
        }

        radioGroupTheme.setEnabled(true);
    }

    @Override
    public void setScreenSaverTimeout(int index) {
        spinnerTimeout.setSelection(index);
        spinnerTimeout.setEnabled(true);
    }
}
