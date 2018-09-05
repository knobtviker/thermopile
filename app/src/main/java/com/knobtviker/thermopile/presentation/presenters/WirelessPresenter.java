package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.knobtviker.thermopile.domain.repositories.NetworkRepository;
import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.WirelessContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;
import com.knobtviker.thermopile.presentation.shared.constants.network.WiFiType;

import javax.inject.Inject;

import io.reactivex.internal.functions.Functions;

/**
 * Created by bojan on 15/07/2017.
 */

public class WirelessPresenter extends AbstractPresenter<WirelessContract.View> implements WirelessContract.Presenter {

    @NonNull
    private final NetworkRepository networkRepository;

    @Inject
    public WirelessPresenter(
        @NonNull final WirelessContract.View view,
        @NonNull final NetworkRepository networkRepository,
        @NonNull final Schedulers schedulers
        ) {
        super(view, schedulers);
        this.networkRepository = networkRepository;
    }

    @Override
    public void hasWifi() {
        compositeDisposable.add(
            networkRepository.hasWifi()
                .subscribe(
                    hasWifi -> {
                        if (hasWifi) {
                            setupWifiDevice();
                        }
                        view.onHasWifi(hasWifi);
                    },
                    this::error
                )
        );
    }

    @Override
    public void isWifiEnabled() {
        compositeDisposable.add(
            networkRepository
                .isWifiEnabled()
                .subscribe(
                    view::onIsWifiEnabled,
                    this::error
                )
        );
    }

    @Override
    public void enableWifi() {
        compositeDisposable.add(
            networkRepository
                .enableWifi()
                .subscribe(
                    () -> view.onIsWifiEnabled(true),
                    this::error
                )
        );
    }

    @Override
    public void scan() {
        compositeDisposable.add(
            networkRepository
                .scanWifi()
                .subscribe(
                    Functions.EMPTY_ACTION,
                    this::error
                )
        );
    }

    @Override
    public void observeScanResults() {
        compositeDisposable.add(
            networkRepository
                .observeScanResults()
                .subscribe(
                    view::onScanResults,
                    this::error
                )
        );
    }

    @Override
    public void connectWifi(@NonNull String ssid, @Nullable String password, @WiFiType String type) {
        compositeDisposable.add(
            networkRepository
                .connectWifi(ssid, password, type)
                .subscribe(
                    view::onConnectWifi,
                    this::error
                )
        );
    }

    private void setupWifiDevice() {
        compositeDisposable.add(
            networkRepository
                .setupWifiDevice()
                .subscribe(
                    this::isWifiEnabled,
                    this::error
                )
        );
    }
}
