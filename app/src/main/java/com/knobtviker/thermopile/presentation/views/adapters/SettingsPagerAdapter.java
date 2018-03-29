package com.knobtviker.thermopile.presentation.views.adapters;

import android.annotation.SuppressLint;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.ImmutableList;

/**
 * Created by bojan on 01/11/2017.
 */

public class SettingsPagerAdapter extends PagerAdapter {

    private final ImmutableList<String> titles;
    private final FragmentManager fragmentManager;
    private final ImmutableList<Fragment> fragments;

    private FragmentTransaction currentTransaction;

    public SettingsPagerAdapter(@NonNull final FragmentManager fragmentManager, @NonNull final ImmutableList<String> titles, @NonNull final ImmutableList<Fragment> fragments, final int containerId) {
        if (containerId == View.NO_ID) {
            throw new IllegalStateException("ViewPager with adapter " + this + " requires a view id");
        }

        this.fragmentManager = fragmentManager;
        this.titles = titles;
        this.fragments = fragments;

        final FragmentTransaction transaction = this.fragmentManager.beginTransaction();
        this.fragments.forEach(fragment -> transaction.add(containerId, fragment));
        transaction.commitNowAllowingStateLoss();
    }

    @SuppressLint("CommitTransaction")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction();
        }

        final Fragment fragment = fragments.get(position);

        currentTransaction.show(fragments.get(position));

        return fragment;
    }

    @SuppressLint("CommitTransaction")
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction();
        }
        currentTransaction.hide((Fragment) object);
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        if (currentTransaction != null) {
            currentTransaction.commitNowAllowingStateLoss();
            currentTransaction = null;
        }
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position).toUpperCase();
    }
}
