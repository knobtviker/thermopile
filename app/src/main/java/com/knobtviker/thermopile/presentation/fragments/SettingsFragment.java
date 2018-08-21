package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knobtviker.thermopile.BuildConfig;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.ThermopileApplication;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;

import java.util.Objects;

import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class SettingsFragment extends BaseFragment {

    public static final String TAG = SettingsFragment.class.getSimpleName();

    @BindView(R.id.bottom_navigation_view)
    public BottomNavigationView bottomNavigationView;

    public SettingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupBottomNavigationView();
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

    @OnClick({R.id.button_back, R.id.button_help, R.id.button_feedback, R.id.button_about})
    public void onClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.button_back:
                NavHostFragment.findNavController(this).navigateUp();
                break;
            case R.id.button_help:
                Timber.i("Show HelpActivity");
                ((ThermopileApplication) requireActivity().getApplication()).reset();
                break;
            case R.id.button_feedback:
                Timber.i("Show FeedbackActivity");
                //                NavHostFragment.findNavController(this).navigate(R.id.activityScreensaver);
                break;
            case R.id.button_about:
                Timber.i("Version name: " + BuildConfig.VERSION_NAME + " Version code: " + BuildConfig.VERSION_CODE + " Hash: "
                    + BuildConfig.GIT_COMMIT_SHA + " Build time: " + BuildConfig.GIT_COMMIT_TIMESTAMP + " Reboot count: "
                    + ThermopileApplication.bootCount() + " Last boot timestamp: " + ThermopileApplication.lastBootTimestamp());
                break;
        }
    }

    private void setupBottomNavigationView() {
        final NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.navigation_host_settings);
        if (navHostFragment != null) {
            NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());
        } else {
            showError(new Throwable("Cannot find NavHostFragment"));
        }
    }
}
