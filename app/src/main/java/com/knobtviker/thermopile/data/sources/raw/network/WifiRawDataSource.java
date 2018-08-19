package com.knobtviker.thermopile.data.sources.raw.network;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Completable;
import io.reactivex.Observable;

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
            ));
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
            ));
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
            ));
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
            ));
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
            ));
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
            ));
    }
}
