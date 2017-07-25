package com.knobtviker.thermopile.presentation.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.presentation.Threshold;
import com.knobtviker.thermopile.presentation.views.viewholders.HourViewHolder;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by bojan on 13/06/2017.
 */

public class HoursAdapter extends RecyclerView.Adapter<HourViewHolder> {

    private final LayoutInflater layoutInflater;
    private final ImmutableList<Integer> hours;
    private ImmutableList<Threshold> thresholds = ImmutableList.of();

    private RecyclerView recyclerView;

    public HoursAdapter(@NonNull final Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.hours = ImmutableList.copyOf(
            IntStream
                .range(0, 24)
                .boxed()
                .collect(Collectors.toList())
        );
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView = recyclerView;
    }

    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new HourViewHolder(layoutInflater.inflate(R.layout.item_hour, null));
    }

    @Override
    public void onBindViewHolder(HourViewHolder hourViewHolder, int position) {
        final int hour = hours.get(position);

        hourViewHolder.textViewHour.setText(String.format(hour < 10 ? "0%s" : "%s", String.valueOf(hour)));
    }

    @Override
    public void onViewRecycled(HourViewHolder holder) {

        holder.viewIndicator.setBackgroundColor(ContextCompat.getColor(recyclerView.getContext(), android.R.color.transparent));

        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    public void applyThreasholds(@NonNull final ImmutableList<Threshold> thresholds) {
        this.thresholds = thresholds;

        this.thresholds
            .forEach(threshold -> {
                final ImmutableList<Integer> thresholdHours = ImmutableList.copyOf(
                    IntStream
                        .range(threshold.startHour(), threshold.endHour() + 1)
                        .boxed()
                        .collect(Collectors.toList())
                );

                thresholdHours.forEach(
                    hour -> ((HourViewHolder) recyclerView.findViewHolderForLayoutPosition(hour)).viewIndicator.setBackgroundColor(threshold.color())
                );
            });
    }
}
