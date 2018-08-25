package com.knobtviker.thermopile.presentation.views.adapters;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.views.viewholders.WirelessViewHolder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

/**
 * Created by bojan on 25/08/2018.
 */

public class WirelessAdapter extends RecyclerView.Adapter<WirelessViewHolder> {

    private static final int MAX_LEVELS = 5;
    private static final String WPA2 = "WPA2";
    private static final String WPA = "WPA";
    private static final String WEP = "WEP";
    private static final String OPEN = "Open";
    private static final String WPA_EAP = "WPA-EAP";
    private static final String IEEE8021X = "IEEE8021X";

    private List<ScanResult> items = Collections.emptyList();

    public WirelessAdapter() {

    }

    @NonNull
    @Override
    public WirelessViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new WirelessViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_wireless, null));
    }

    @Override
    public void onBindViewHolder(@NonNull WirelessViewHolder viewHolder, int position) {
        final ScanResult item = items.get(position);
        final boolean isSecured = !Objects.equals(securityMode(item.capabilities), OPEN);
        final int level = WifiManager.calculateSignalLevel(item.level, MAX_LEVELS);

        viewHolder.imageViewIcon.setImageResource(fromRSSI(level, isSecured));
        viewHolder.textViewSSID.setText(item.SSID);
        Timber.i("%d %s", level, item.SSID);
    }

    @Override
    public void onViewRecycled(@NonNull WirelessViewHolder viewHolder) {
        super.onViewRecycled(viewHolder);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<ScanResult> scanResults) {
        this.items = scanResults;
        notifyDataSetChanged();
    }

    private int fromRSSI(final int level, final boolean isSecured) {
        switch (level) {
            case 0:
                return isSecured ? R.drawable.ic_wifi_signal_0 : R.drawable.ic_wifi_signal_0;
            case 1:
                return isSecured ? R.drawable.ic_wifi_signal_1_locked : R.drawable.ic_wifi_signal_1;
            case 2:
                return isSecured ? R.drawable.ic_wifi_signal_2_locked : R.drawable.ic_wifi_signal_2;
            case 3:
                return isSecured ? R.drawable.ic_wifi_signal_3_locked : R.drawable.ic_wifi_signal_3;
            case 4:
                return isSecured ? R.drawable.ic_wifi_signal_4_locked : R.drawable.ic_wifi_signal_4;
            default:
                return isSecured ? R.drawable.ic_wifi_signal_0 : R.drawable.ic_wifi_signal_0;
        }
    }

    private String securityMode(@NonNull final String cap) {
        final String[] securityModes = {WEP, WPA, WPA2, WPA_EAP, IEEE8021X};
        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (cap.contains(securityModes[i])) {
                return securityModes[i];
            }
        }

        return OPEN;
    }
}
