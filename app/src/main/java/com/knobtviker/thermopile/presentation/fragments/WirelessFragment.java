package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.contracts.WirelessContract;
import com.knobtviker.thermopile.presentation.presenters.WirelessPresenter;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;

import java.util.Objects;

import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by bojan on 28/10/2017.
 */

public class WirelessFragment extends BaseFragment<WirelessContract.Presenter> implements WirelessContract.View {
    public static String TAG = WirelessFragment.class.getSimpleName();

    @BindView(R.id.recyclerview_networks)
    public RecyclerView recyclerViewNetworks;

    public WirelessFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wireless, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        presenter = new WirelessPresenter(this);

        load();
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);

        Snackbar.make(Objects.requireNonNull(getView()), throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

    @OnClick({R.id.button_discard, R.id.button_save})
    public void onClicked(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.button_discard:
                back();
                break;
            case R.id.button_save:
//                save();
                break;
        }
    }

    private void setupRecyclerView() {
        recyclerViewNetworks.setHasFixedSize(true);
        recyclerViewNetworks.setLayoutManager(new LinearLayoutManager(requireContext()));
//        recyclerViewColors.setAdapter(colorAdapter);
    }

    private void load() {

    }

    private void back() {
        NavHostFragment.findNavController(this).navigateUp();
    }
}
