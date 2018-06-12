package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.di.components.presentation.presenters.DaggerSettingsPresenterComponent;
import com.knobtviker.thermopile.presentation.contracts.SettingsContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.SettingsPresenter;
import com.knobtviker.thermopile.presentation.views.adapters.SettingsPagerAdapter;

import java.util.Arrays;
import java.util.List;

import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class SettingsFragment extends BaseFragment<SettingsContract.Presenter> implements SettingsContract.View {
    public static final String TAG = SettingsFragment.class.getSimpleName();

    private final FormatsFragment formatsFragment;
    private final UnitsFragment unitsFragment;
    private final LocaleFragment localeFragment;
    private final StyleFragment styleFragment;
    private final NetworkFragment networkFragment;

    @BindView(R.id.tab_layout)
    public TabLayout tabLayout;

    @BindView(R.id.view_pager)
    public ViewPager viewPager;

    public SettingsFragment() {
        this.localeFragment = LocaleFragment.newInstance();
        this.formatsFragment = FormatsFragment.newInstance();
        this.unitsFragment = UnitsFragment.newInstance();
        this.styleFragment = StyleFragment.newInstance();
        this.networkFragment = NetworkFragment.newInstance();

        this.presenter = new SettingsPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewPager();
    }

    @Override
    public void onResume() {
        presenter.load();

        super.onResume();
    }

    @Override
    public void onLoad(@NonNull Settings settings) {
        localeFragment.onLoad(settings);
        formatsFragment.onLoad(settings);
        unitsFragment.onLoad(settings);
        styleFragment.onLoad(settings);
    }

    @Override
    public void showLoading(boolean isLoading) {
        //TODO: Show loading progress indicator
    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);
    }

    @OnClick({R.id.button_back, R.id.button_help, R.id.button_feedback, R.id.button_about})
    public void onClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.button_back:
                NavHostFragment.findNavController(this).navigate(R.id.action_settingsFragment_to_mainFragment);
                break;
            case R.id.button_help:
                Timber.i("Show HelpActivity");
                break;
            case R.id.button_feedback:
                Timber.i("Show FeedbackActivity");
                break;
            case R.id.button_about:
                Timber.i("Show AboutActivity");
                break;
        }
    }

    private void setupViewPager() {
        viewPager.setAdapter(new SettingsPagerAdapter(
                getChildFragmentManager(),
                Arrays.asList(getString(R.string.label_formats), getString(R.string.label_units), getString(R.string.label_locale), getString(R.string.label_style), getString(R.string.label_network)),
                Arrays.asList(formatsFragment, unitsFragment, localeFragment, styleFragment, networkFragment),
                viewPager.getId()
            )
        );
        if (viewPager.getAdapter() != null) {
            viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount());
        }

        tabLayout.setupWithViewPager(viewPager);
    }
}
