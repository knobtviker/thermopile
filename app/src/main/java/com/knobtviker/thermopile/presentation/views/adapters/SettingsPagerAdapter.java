package com.knobtviker.thermopile.presentation.views.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.presentation.fragments.SettingsFormatFragment;
import com.knobtviker.thermopile.presentation.fragments.SettingsLocationFragment;
import com.knobtviker.thermopile.presentation.fragments.SettingsUnitFragment;

/**
 * Created by bojan on 01/11/2017.
 */

public class SettingsPagerAdapter extends FragmentPagerAdapter {

    private ImmutableList<String> titles = ImmutableList.of();

    private ImmutableList<Fragment> fragments = ImmutableList.of();

    public SettingsPagerAdapter(@NonNull final FragmentManager fragmentManager, @NonNull final ImmutableList<String> titles) {
        super(fragmentManager);

        this.titles = titles;

        fragments = ImmutableList.of(
            SettingsLocationFragment.newInstance(),
            SettingsFormatFragment.newInstance(),
            SettingsUnitFragment.newInstance()
        );
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position).toUpperCase();
    }
}
