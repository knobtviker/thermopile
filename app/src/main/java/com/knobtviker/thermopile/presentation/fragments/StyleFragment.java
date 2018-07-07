package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.StyleContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.StylePresenter;
import com.knobtviker.thermopile.presentation.utils.constants.Default;
import com.knobtviker.thermopile.presentation.utils.constants.ScreensaverTimeout;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class StyleFragment extends BaseFragment<StyleContract.Presenter> implements StyleContract.View {
    public static final String TAG = StyleFragment.class.getSimpleName();

    private ArrayAdapter<String> spinnerAdapterTimeout;

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

    public static StyleFragment newInstance() {
        return new StyleFragment();
    }

    public StyleFragment() {
        presenter = new StylePresenter(this);
    }

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
        this.theme = settings.theme;
        this.screenSaverTimeout = settings.screensaverDelay;

        setTheme();
        setScreenSaverTimeout();
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

        final List<String> formats = Arrays.asList(
            String.valueOf(ScreensaverTimeout._15S),
            String.valueOf(ScreensaverTimeout._30S),
            String.valueOf(ScreensaverTimeout._1MIN),
            String.valueOf(ScreensaverTimeout._2MIN),
            String.valueOf(ScreensaverTimeout._5MIN),
            String.valueOf( ScreensaverTimeout._10MIN)
        );
        spinnerAdapterTimeout = new ArrayAdapter<>(spinnerTimeout.getContext(), android.R.layout.simple_spinner_item, formats);
        spinnerAdapterTimeout.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
//        for (int i = 0; i < spinnerAdapterTimeout.getCount(); i++) {
//            if (spinnerAdapterTimeout.getItem(i) == screenSaverTimeout) {
//                spinnerTimeout.setSelection(i);
//                break;
//            }
//        }

        spinnerTimeout.setEnabled(true);
    }
}
