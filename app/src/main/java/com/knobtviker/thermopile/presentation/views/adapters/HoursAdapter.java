package com.knobtviker.thermopile.presentation.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.annimon.stream.IntStream;
import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.views.viewholders.HourViewHolder;

/**
 * Created by bojan on 13/06/2017.
 */

public class HoursAdapter extends RecyclerView.Adapter<HourViewHolder> {

    private final ImmutableList<Integer> hours;

    private final LayoutInflater layoutInflater;

    public HoursAdapter(@NonNull final Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.hours = ImmutableList.copyOf(
            IntStream
                .range(0, 24)
                .boxed()
                .toList()
        );
    }

    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new HourViewHolder(layoutInflater.inflate(R.layout.item_hour, null));
    }

    @Override
    public void onBindViewHolder(HourViewHolder hourViewHolder, int i) {
        final int hour = hours.get(i);
        hourViewHolder.textViewHour.setText(String.format(hour < 10 ? "0%s" : "%s", String.valueOf(hour)));
    }

    @Override
    public int getItemCount() {
        return hours.size();
    }
}
