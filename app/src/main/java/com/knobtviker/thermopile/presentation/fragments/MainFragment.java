package com.knobtviker.thermopile.presentation.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.sources.raw.implementation.drivers.Bme280;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.views.CircularSeekBar;
import com.knobtviker.thermopile.presentation.views.adapters.HoursAdapter;
import com.knobtviker.thermopile.presentation.views.communicators.MainCommunicator;

import java.io.IOException;

import butterknife.BindView;

/**
 * Created by bojan on 09/06/2017.
 */

public class MainFragment extends BaseFragment {
    public static final String TAG = MainFragment.class.getSimpleName();

    private MainCommunicator mainCommunicator;

    private Bme280 bme280;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.textview_clock)
    public TextView textViewClock;

    @BindView(R.id.seekbar_temperature)
    public CircularSeekBar seekBarTemperature;

    @BindView(R.id.textview_current_temperature)
    public TextView textViewCurrentTemperature;

    @BindView(R.id.recyclerview_hours)
    public RecyclerView recyclerView;

    public static Fragment newInstance() {
        return new MainFragment();
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
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        bind(this, view);

        setupToolbar();
        setupClock();
        setupRecyclerView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        testInit();
        testRead();
    }

    @Override
    public void onPause() {
        super.onPause();

        testClose();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_modes) {
            mainCommunicator.showModes();
        } else if (item.getItemId() == R.id.action_settings) {
            mainCommunicator.showSettings();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mainCommunicator = null;
    }

    private void setupToolbar() {
        setupCustomActionBar(toolbar);
    }

    private void setupClock() {
        textViewClock.setText("23:59"); //TODO: Change from hardcoded to real fetched RXJava2 value.
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new HoursAdapter(this.getContext()));

        final SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    private void testInit() {
        try {
            bme280 = new Bme280("I2C1");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    private void testRead() {
        try {
            final float[] readings = bme280.readAll();
            Log.i(TAG, "Temperature: " + readings[0] + " --- Humidity: " + readings[1] + " --- Pressure: " + readings[1]);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void testClose() {
        try {
            bme280.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
