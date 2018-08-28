package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.StyleContract;
import com.knobtviker.thermopile.presentation.presenters.StylePresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.shared.constants.integrity.Default;
import com.knobtviker.thermopile.presentation.shared.constants.settings.ScreensaverTimeout;
import com.knobtviker.thermopile.presentation.views.adapters.TimeoutAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnItemSelected;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class StyleFragment extends BaseFragment<StyleContract.Presenter> implements StyleContract.View {
    public static final String TAG = StyleFragment.class.getSimpleName();

    private TimeoutAdapter spinnerAdapterTimeout;

    private long settingsId = -1L;

    private int theme = Default.THEME;

    @ScreensaverTimeout
    private int screenSaverTimeout = ScreensaverTimeout._1MIN;

    @BindView(R.id.radiogroup_theme)
    public RadioGroup radioGroupTheme;

    @BindView(R.id.light)
    public RadioButton radioButtonLight;

    @BindView(R.id.dark)
    public RadioButton radioButtonDark;

    @BindView(R.id.automatic)
    public RadioButton radioButtonAutomatic;

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

        setupRadioGroupTheme();
        setupSpinnerTimeout();

        presenter = new StylePresenter(this);

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
        this.theme = settings.theme;
        this.screenSaverTimeout = settings.screensaverDelay;

        setTheme();
        setScreenSaverTimeout();
    }

    @OnItemSelected(value = {R.id.spinner_timeout}, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onItemSelected(@NonNull final AdapterView<?> adapterView, @NonNull final View view, final int position, final long id) {
        switch (adapterView.getId()) {
            case R.id.spinner_timeout:
                if (spinnerTimeout.isEnabled() && spinnerAdapterTimeout != null && spinnerAdapterTimeout.getItem(position) > 0) {
                    screenSaverTimeout = spinnerAdapterTimeout.getItem(position);
                    if (screenSaverTimeout > 0) {
                        presenter.saveScreensaverTimeout(settingsId, screenSaverTimeout);
                    }
                }
                break;
        }
    }

    private void setupRadioGroupTheme() {
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
                AppCompatDelegate.setDefaultNightMode(value);
                this.theme = value;
                presenter.saveTheme(settingsId, value);

                requireActivity().recreate();
            }
        });
    }

    private void setupSpinnerTimeout() {
        spinnerTimeout.setEnabled(false);
        spinnerTimeout.setPrompt("Screensaver Timeout");

        final List<Integer> timeouts = Arrays.asList(
            ScreensaverTimeout._15S,
            ScreensaverTimeout._30S,
            ScreensaverTimeout._1MIN,
            ScreensaverTimeout._2MIN,
            ScreensaverTimeout._5MIN,
            ScreensaverTimeout._10MIN
        );
        spinnerAdapterTimeout = new TimeoutAdapter(requireContext(), timeouts);
        spinnerTimeout.setAdapter(spinnerAdapterTimeout);
    }

    private void setTheme() {
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

    private void setScreenSaverTimeout() {
        for (int i = 0; i < spinnerAdapterTimeout.getCount(); i++) {
            if (spinnerAdapterTimeout.getItem(i) == screenSaverTimeout) {
                spinnerTimeout.setSelection(i);
                break;
            }
        }

        spinnerTimeout.setEnabled(true);
    }
}
