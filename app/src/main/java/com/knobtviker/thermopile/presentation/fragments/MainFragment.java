package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.views.KnobButton;
import com.knobtviker.thermopile.presentation.views.adapters.HoursAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by bojan on 09/06/2017.
 */

public class MainFragment extends Fragment {
    public static final String TAG = MainFragment.class.getSimpleName();

    private Unbinder unbinder;

    @BindView(R.id.recyclerview_hours)
    public RecyclerView recyclerViewHours;

    public static Fragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        unbinder = ButterKnife.bind(this, view);

        setupRecyclerViewHours();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    private void setupRecyclerViewHours() {
        recyclerViewHours.setHasFixedSize(true);
        recyclerViewHours.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewHours.setAdapter(new HoursAdapter(this.getContext()));

        final SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewHours);
    }
}
