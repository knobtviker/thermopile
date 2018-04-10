package com.knobtviker.thermopile.data.sources.local.implemenatation;

import android.util.Log;

import io.realm.CompactOnLaunchCallback;

public class CompactCallback implements CompactOnLaunchCallback {
    private final String TAG = CompactCallback.class.getSimpleName();

    private final static long THRESHOLD_SIZE = 50 * 1024 * 1024;
    private final static double RATIO = 0.8;

    private final long thresholdSize;
    private final double ratio;

    public static CompactCallback create() {
        return new CompactCallback(THRESHOLD_SIZE, RATIO);
    }

    public static CompactCallback create(final long thresholdSize, final double ratio) {
        return new CompactCallback(thresholdSize, ratio);
    }

    private CompactCallback(final long thresholdSize, final double ratio) {
        this.thresholdSize = thresholdSize;
        this.ratio = ratio;
    }

    @Override
    public boolean shouldCompact(long totalBytes, long usedBytes) {
        final boolean result = (totalBytes > thresholdSize) && (((double) usedBytes / (double) totalBytes) < ratio);
        Log.i(TAG, "shouldCompact -> " + thresholdSize + "/" + ratio + " -> " + result + " -> totalBytes: " + totalBytes + " usedBytes: " + usedBytes + " claimed ratio: " + ((double) usedBytes / (double) totalBytes));
        return result;
    }
}
