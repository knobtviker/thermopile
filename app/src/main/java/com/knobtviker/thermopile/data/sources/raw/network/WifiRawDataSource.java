package com.knobtviker.thermopile.data.sources.raw.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.MainThreadDisposable;

public class WifiRawDataSource {

    //    @Nullable
    //    private final WifiManager wifiManager;

    @NonNull
    private final Context context;

    @Nullable
    private WifiManager wifiManager;

    public WifiRawDataSource(@NonNull final Context context) {
        this.context = context;
    }

    public Observable<Boolean> hasWifi() {
        return Observable.defer(() ->
            Observable.create(
                emitter -> {
                    if (!emitter.isDisposed()) {
                        try {
                            emitter.onNext(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI));
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                        emitter.onComplete();
                    }
                }
            )
        );
    }

    public Completable setup() {
        return Completable.defer(() ->
            Completable.create(
                emitter -> {
                    if (!emitter.isDisposed()) {
                        try {
                            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                            emitter.onComplete();
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }
            )
        );
    }

    public Observable<Boolean> isEnabled() {
        return Observable.defer(() ->
            Observable.create(
                emitter -> {
                    if (!emitter.isDisposed()) {
                        try {
                            emitter.onNext(wifiManager != null && wifiManager.isWifiEnabled());
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                        emitter.onComplete();
                    }
                }
            )
        );
    }

    public Observable<WifiInfo> wifiInfo() {
        return Observable.defer(() ->
            Observable.create(
                emitter -> {
                    if (!emitter.isDisposed()) {
                        try {
                            if (wifiManager != null) {
                                emitter.onNext(wifiManager.getConnectionInfo());
                            } else {
                                emitter.onError(new IllegalStateException());
                            }
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                        emitter.onComplete();
                    }
                }
            )
        );
    }

    public Completable enable() {
        return Completable.defer(() ->
            Completable.create(
                emitter -> {
                    if (!emitter.isDisposed()) {
                        try {
                            if (wifiManager != null) {
                                wifiManager.setWifiEnabled(true);
                            } else {
                                emitter.onError(new IllegalStateException());
                            }
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }
            )
        );
    }

    public Completable disable() {
        return Completable.defer(() ->
            Completable.create(
                emitter -> {
                    if (!emitter.isDisposed()) {
                        try {
                            if (wifiManager != null) {
                                wifiManager.setWifiEnabled(false);
                            } else {
                                emitter.onError(new IllegalStateException());
                            }
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }
            )
        );
    }

    public Completable scan() {
        return Completable.defer(() ->
            Completable.create(
                emitter -> {
                    if (!emitter.isDisposed()) {
                        try {
                            if (wifiManager != null) {
                                wifiManager.startScan();
                                emitter.onComplete();
                            } else {
                                emitter.onError(new IllegalStateException());
                            }
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    }
                }
            )
        );
    }

    public Observable<List<ScanResult>> scanResults() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        return Observable.defer(() ->
            Observable.create(emitter -> {
                final BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.hasExtra(WifiManager.EXTRA_RESULTS_UPDATED)) {
                            if(intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)) {
                                if (wifiManager != null) {
                                    emitter.onNext(wifiManager.getScanResults());
                                } else {
                                    emitter.onError(new IllegalStateException());
                                }
                            }
                        }
                    }
                };

                context.registerReceiver(receiver, filter);

                emitter.setDisposable(new MainThreadDisposable() {
                    @Override
                    protected void onDispose() {
                        context.unregisterReceiver(receiver);
                        dispose();
                    }
                });
            }));
    }
}
