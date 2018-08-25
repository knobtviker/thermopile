package com.knobtviker.thermopile.data.sources.raw.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.knobtviker.thermopile.presentation.shared.constants.network.WiFiType;

import java.util.List;
import java.util.Optional;

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
                            if (intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)) {
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
            })
        );
    }

    public Observable<Boolean> connect(@NonNull final String ssid, @Nullable final String password, @WiFiType final String type) {
        return Observable.defer(() ->
            Observable.create(emitter -> {
                if (!emitter.isDisposed()) {
                    if (password == null && !type.equalsIgnoreCase(WiFiType.OPEN)) {
                        emitter.onError(new IllegalStateException());
                        emitter.onComplete();
                        return;
                    }

                    if (wifiManager != null) {
                        final List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
                        final Optional<WifiConfiguration> optionalWifiConfiguration = configurations
                            .stream()
                            .filter(configuration -> configuration.SSID.equals("\"".concat(ssid).concat("\"")))
                            .findFirst();

                        if (optionalWifiConfiguration.isPresent()) {
                            final WifiConfiguration wifiConfiguration = optionalWifiConfiguration.get();

                            switch (type) {
                                case WiFiType.OPEN:
                                    break;
                                case WiFiType.WEP:
                                    wifiConfiguration.wepKeys[0] = "\"".concat(password).concat("\"");
                                    wifiConfiguration.wepTxKeyIndex = 0;
                                    break;
                                case WiFiType.WPA:
                                case WiFiType.WPA2:
                                    wifiConfiguration.preSharedKey = "\"".concat(password).concat("\"");
                                    break;
                                default:
                                    emitter.onError(new IllegalStateException());
                                    break;
                            }

                            if (wifiConfiguration.networkId != -1) {
                                boolean result = false;
                                result = wifiManager.disconnect();
                                result = wifiManager.enableNetwork(wifiConfiguration.networkId, true);
                                result = wifiManager.reconnect();
                                emitter.onNext(result);
                            }
                        } else {
                            emitter.onError(new IllegalStateException());
                        }
                    } else {
                        emitter.onError(new IllegalStateException());
                    }
/*


                    wifiConfiguration.SSID = "\"".concat(ssid).concat("\"");
                    wifiConfiguration.status = WifiConfiguration.Status.DISABLED;

                    switch (type) {
                        case WiFiType.OPEN:
                            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                            wifiConfiguration.allowedAuthAlgorithms.clear();
                            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                            break;
                        case WiFiType.WEP:
                            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

                            wifiConfiguration.wepKeys[0] = "\"".concat(password).concat("\"");
                            wifiConfiguration.wepTxKeyIndex = 0;
                            break;
                        case WiFiType.WPA:
                        case WiFiType.WPA2:
                            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

                            wifiConfiguration.preSharedKey = "\"".concat(password).concat("\"");
                            break;
                            default:
                                emitter.onError(new IllegalStateException());
                                break;
                    }

                    if (wifiManager != null) {
                        final int networkId = wifiManager.addNetwork(wifiConfiguration);
                        if (networkId != -1) {
                            boolean result = false;
                            result = wifiManager.disconnect();
                            result = wifiManager.enableNetwork(networkId, true);
                            result = wifiManager.reconnect();
                            emitter.onNext(result);
                        }
                    } else {
                        emitter.onError(new IllegalStateException());
                    }
                    */

                    emitter.onComplete();
                }
            })
        );
    }
}
