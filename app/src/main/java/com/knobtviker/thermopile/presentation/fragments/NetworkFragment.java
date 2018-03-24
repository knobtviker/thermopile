package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.contracts.NetworkContract;
import com.knobtviker.thermopile.presentation.fragments.implementation.BaseFragment;
import com.knobtviker.thermopile.presentation.presenters.NetworkPresenter;

/**
 * Created by bojan on 15/06/2017.
 */

public class NetworkFragment extends BaseFragment<NetworkContract.Presenter> implements NetworkContract.View {
    public static final String TAG = NetworkFragment.class.getSimpleName();

    private long settingsId = -1L;

    public static NetworkFragment newInstance() {
        return new NetworkFragment();
    }

    public NetworkFragment() {
        presenter = new NetworkPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_network, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bind(this, view);


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Log.e(TAG, throwable.getMessage(), throwable);
    }
}
