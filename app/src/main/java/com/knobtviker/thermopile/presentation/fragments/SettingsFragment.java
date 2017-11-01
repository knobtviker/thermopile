package com.knobtviker.thermopile.presentation.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.views.adapters.SettingsPagerAdapter;
import com.knobtviker.thermopile.presentation.views.communicators.MainCommunicator;

import butterknife.BindView;

/**
 * Created by bojan on 15/06/2017.
 */

public class SettingsFragment extends BaseFragment {
    public static final String TAG = SettingsFragment.class.getSimpleName();

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);

        bind(this, view);

        setupToolbar();
        setupViewPager();

        return view;
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mainCommunicator = null;
    }

    private void setupToolbar() {
        setupCustomActionBarWithHomeAsUp(toolbar);
    }

    private void setupViewPager() {
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new SettingsPagerAdapter(getChildFragmentManager(), ImmutableList.of("Location", "Formats", "Units")));

        tabLayout.setupWithViewPager(viewPager);
    }
}
