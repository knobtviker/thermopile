package com.knobtviker.thermopile.presentation.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.Settings;
import com.knobtviker.thermopile.presentation.contracts.SettingsContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.SettingsPresenter;
import com.knobtviker.thermopile.presentation.views.adapters.SettingsPagerAdapter;
import com.knobtviker.thermopile.presentation.views.communicators.MainCommunicator;

import butterknife.BindView;

/**
 * Created by bojan on 15/06/2017.
 */

public class SettingsFragment extends BaseFragment<SettingsContract.Presenter> implements SettingsContract.View {
    public static final String TAG = SettingsFragment.class.getSimpleName();

    private final LocaleFragment regionFragment;
    private final FormatFragment formatFragment;
    private final UnitFragment unitFragment;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.tab_layout)
    public TabLayout tabLayout;

    @BindView(R.id.view_pager)
    public ViewPager viewPager;

    @Nullable
    private MainCommunicator mainCommunicator;

    public static Fragment newInstance() {
        return new SettingsFragment();
    }

    public SettingsFragment() {
        this.regionFragment = LocaleFragment.newInstance();
        this.formatFragment = FormatFragment.newInstance();
        this.unitFragment = UnitFragment.newInstance();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainCommunicator) {
            mainCommunicator = (MainCommunicator) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        presenter = new SettingsPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bind(this, view);

        setupToolbar();
        setupViewPager();

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        presenter.load(realm);

        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mainCommunicator != null) {
                mainCommunicator.back();
            }
            return true;
        }
        if (item.getItemId() == R.id.action_help) {
            //TODO: Show help activity
            return true;
        }
        if (item.getItemId() == R.id.action_about) {
            //TODO: Show about app activity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mainCommunicator = null;
    }

    @Override
    public void onLoad(@NonNull Settings settings) {
        regionFragment.onLoad(settings);
        formatFragment.onLoad(settings);
        unitFragment.onLoad(settings);
    }

    @Override
    public void showLoading(boolean isLoading) {
        //TODO: Show loading progress indicator
    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Log.e(TAG, throwable.getMessage(), throwable);
    }

    private void setupToolbar() {
        setupCustomActionBarWithHomeAsUp(toolbar);
    }

    private void setupViewPager() {
        viewPager.setAdapter(new SettingsPagerAdapter(
                getChildFragmentManager(),
                ImmutableList.of(getString(R.string.label_locale), getString(R.string.label_format), getString(R.string.label_unit)),
                ImmutableList.of(regionFragment, formatFragment, unitFragment)
            )
        );
        if (viewPager.getAdapter() != null) {
            viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount());
        }

        tabLayout.setupWithViewPager(viewPager);
    }
}
