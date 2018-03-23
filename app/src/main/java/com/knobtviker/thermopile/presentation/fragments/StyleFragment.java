package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.StyleContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.StylePresenter;

import butterknife.BindView;

/**
 * Created by bojan on 15/06/2017.
 */

public class StyleFragment extends BaseFragment<StyleContract.Presenter> implements StyleContract.View {
    public static final String TAG = StyleFragment.class.getSimpleName();

    private long settingsId = -1L;

    private int currentNightMode;

    @BindView(R.id.radiogroup_theme)
    public RadioGroup radioGroupTheme;

    @BindView(R.id.light)
    public RadioButton radioButtonLight;

    @BindView(R.id.dark)
    public RadioButton radioButtonDark;

    @BindView(R.id.automatic)
    public RadioButton radioButtonAutomatic;

    public static StyleFragment newInstance() {
        return new StyleFragment();
    }

    public StyleFragment() {
        presenter = new StylePresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_style, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bind(this, view);

        setupRadioGroupTheme();

        super.onViewCreated(view, savedInstanceState);
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

        switch (settings.theme()) {
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
                presenter.saveTheme(settingsId, value);
                getActivity().recreate();
            }
        });
    }
}
