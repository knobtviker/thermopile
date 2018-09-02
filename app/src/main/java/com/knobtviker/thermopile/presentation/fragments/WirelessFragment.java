package com.knobtviker.thermopile.presentation.fragments;

import android.net.wifi.ScanResult;
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
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;
import com.knobtviker.thermopile.presentation.shared.constants.network.WiFiType;
import com.knobtviker.thermopile.presentation.views.adapters.WirelessAdapter;
import com.knobtviker.thermopile.presentation.views.dialogs.BottomDialog;
import com.knobtviker.thermopile.presentation.views.listeners.WirelessSelectListener;
import com.knobtviker.thermopile.presentation.views.viewholders.ConnecDialogViewHolder;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by bojan on 28/10/2017.
 */

public class WirelessFragment extends BaseFragment<WirelessContract.Presenter> implements WirelessContract.View, WirelessSelectListener {

    @NonNull
    private WirelessAdapter adapter;

    @BindView(R.id.recyclerview_networks)
    public RecyclerView recyclerViewNetworks;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wireless, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
    }

    @Override
    protected void load() {
        presenter.hasWifi();
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);

        Snackbar.make(Objects.requireNonNull(getView()), throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

    private void setupRecyclerView() {
        adapter = new WirelessAdapter(this);
        recyclerViewNetworks.setHasFixedSize(true);
        recyclerViewNetworks.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewNetworks.setAdapter(adapter);
    }

    private void back() {
        requireActivity().finish();
    }

    @Override
    public void onHasWifi(boolean hasWifi) {
        // ... do something
    }

    @Override
    public void onIsWifiEnabled(boolean isEnabled) {
        if (isEnabled) { // do the scan
            presenter.scan();
            presenter.observeScanResults();
        } else {
            presenter.enableWifi(); // enable wifi first and then do the scan (actually show dialog asking user if he wants to scan or
            // dismiss)
        }
    }

    @Override
    public void onScanResults(@NonNull List<ScanResult> scanResults) {
        adapter.updateItems(scanResults);
    }

    @Override
    public void onConnectWifi(boolean isConnected) {
        Timber.i("onConnectWifi: %s", isConnected);
    }

    @Override
    public void onWirelessSelected(@NonNull ScanResult scanResult, @WiFiType String type) {
        if (type.equalsIgnoreCase(WiFiType.OPEN)) {
            presenter.connectWifi(scanResult.SSID, null, type);
        } else {
            final View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_connect, null);
            final ConnecDialogViewHolder viewHolder = new ConnecDialogViewHolder(view);
            viewHolder.textViewSSID.setText(scanResult.SSID);
            viewHolder.buttonConnect.setOnClickListener(
                v -> presenter.connectWifi(scanResult.SSID, viewHolder.editTextPassword.getText().toString(), type)
            );

            final BottomDialog dialog = new BottomDialog(requireContext());
            dialog.setContentView(view);
            dialog.setTitle("Connect2");
            dialog.show();
        }
    }
}
