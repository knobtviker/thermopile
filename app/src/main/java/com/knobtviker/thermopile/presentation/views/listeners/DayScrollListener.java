package com.knobtviker.thermopile.presentation.views.listeners;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import javax.annotation.Nullable;

public class DayScrollListener extends RecyclerView.OnScrollListener {

    @Nullable
    private Listener listener;

    public static DayScrollListener create(@NonNull final Fragment fragment) {
        return new DayScrollListener(fragment);
    }

    private DayScrollListener(@NonNull final Fragment fragment) {
        if (fragment instanceof Listener) {
            this.listener = (Listener) fragment;
        }
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (listener != null) {
            listener.onDayChanged();
        }
    }

    public interface Listener {
        void onDayChanged();
    }
}
