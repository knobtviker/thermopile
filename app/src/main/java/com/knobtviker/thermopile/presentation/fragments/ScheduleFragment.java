package com.knobtviker.thermopile.presentation.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.views.adapters.ModesAdapter;
import com.knobtviker.thermopile.presentation.views.communicators.MainCommunicator;

import butterknife.BindView;

/**
 * Created by bojan on 15/06/2017.
 */

public class ScheduleFragment extends BaseFragment {
    public static final String TAG = ScheduleFragment.class.getSimpleName();

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

//    @BindView(R.id.recyclerview_modes)
//    public RecyclerView recyclerView;

    private MainCommunicator mainCommunicator;

    private ModesAdapter adapter;

    public static Fragment newInstance() {
        return new ScheduleFragment();
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
        final View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        bind(this, view);

        setupToolbar();
        setupRecyclerView();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.modes, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mainCommunicator.back();
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

    private void setupRecyclerView() {
        adapter = new ModesAdapter(this.getContext());

//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext().getDrawable(R.drawable.divider_mode)));
//        recyclerView.setAdapter(adapter);
//
//        final SnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerView);
    }
}
