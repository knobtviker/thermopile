package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;

/**
 * Created by bojan on 15/06/2017.
 */

public class SettingsUnitFragment extends BaseFragment {
    public static final String TAG = SettingsUnitFragment.class.getSimpleName();

    public static Fragment newInstance() {
        return new SettingsUnitFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        
        final View view = inflater.inflate(R.layout.fragment_settings_unit, container, false);

        bind(this, view);

        return view;
    }
}
