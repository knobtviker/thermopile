package com.knobtviker.thermopile.presentation.views.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.common.collect.ImmutableList;

/**
 * Created by bojan on 01/11/2017.
 */

public class SettingsPagerAdapter extends FragmentPagerAdapter {

    private final ImmutableList<String> titles;

    private final ImmutableList<Fragment> fragments;

    public SettingsPagerAdapter(@NonNull final FragmentManager fragmentManager, @NonNull final ImmutableList<String> titles, @NonNull final ImmutableList<Fragment> fragments) {
        super(fragmentManager);

        this.titles = titles;
        this.fragments = fragments;
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
