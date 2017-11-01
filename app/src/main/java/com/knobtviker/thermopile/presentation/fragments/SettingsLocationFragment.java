package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;

import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by bojan on 15/06/2017.
 */

public class SettingsLocationFragment extends BaseFragment {
    public static final String TAG = SettingsLocationFragment.class.getSimpleName();

    @BindView(R.id.spinner_timezone)
    public Spinner spinnerTimezone;

    public static Fragment newInstance() {
        return new SettingsLocationFragment();
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

        final View view = inflater.inflate(R.layout.fragment_settings_location, container, false);

        bind(this, view);

        setupSpinnerTimezone();

        return view;
    }

    private void setupSpinnerTimezone() {
        final List<String> timezones = new ArrayList<>(DateTimeZone.getAvailableIDs());
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, timezones);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimezone.setAdapter(spinnerAdapter);
        spinnerTimezone.setPrompt("Timezone");
    }
}
