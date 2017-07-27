package com.knobtviker.thermopile.presentation.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.presentation.Hour;
import com.knobtviker.thermopile.data.models.presentation.Threshold;
import com.knobtviker.thermopile.presentation.views.viewholders.HourViewHolder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by bojan on 13/06/2017.
 */

public class HoursAdapter extends RecyclerView.Adapter<HourViewHolder> {

    private final LayoutInflater layoutInflater;
    private final int colorTransparent;

    private List<Hour> hours;
    private ImmutableList<Threshold> thresholds = ImmutableList.of();

    public HoursAdapter(@NonNull final Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.colorTransparent = ContextCompat.getColor(context, android.R.color.transparent);

        initHours();
    }

    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new HourViewHolder(layoutInflater.inflate(R.layout.item_hour, null));
    }

    @Override
    public void onBindViewHolder(HourViewHolder hourViewHolder, int position) {
        final Hour hour = hours.get(position);

        hourViewHolder.textViewHour.setText(String.format(hour.hour() < 10 ? "0%s" : "%s", String.valueOf(hour.hour())));
        hourViewHolder.viewIndicator.setBackgroundColor(hour.color());
        hourViewHolder.viewIndicator.setX(hour.startMinutes());
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)hourViewHolder.viewIndicator.getLayoutParams();
        layoutParams.width = hour.endMinutes() - hour.startMinutes();
        hourViewHolder.viewIndicator.setLayoutParams(layoutParams);
    }

    @Override
    public void onViewRecycled(HourViewHolder holder) {

        holder.viewIndicator.setBackgroundColor(colorTransparent);

        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    public void applyThreasholds(@NonNull final ImmutableList<Threshold> thresholds) {
        this.thresholds = thresholds;

        initHours();

        this.thresholds
            .forEach(threshold -> {
                final int startHour = threshold.startHour();
                final int startMinute = threshold.startMinute();
                final int endHour = threshold.endHour();
                final int endMinute = threshold.endMinute();

                final ImmutableList<Integer> thresholdHours = ImmutableList.copyOf(
                    IntStream
                        .range(threshold.startHour(), threshold.endHour() + 1)
                        .boxed()
                        .collect(Collectors.toList())
                );

                thresholdHours
                    .forEach(position -> {
                        Hour hour = hours.get(position);
                        if (position == startHour) {
                            hour = hour.withStartMinutes(startMinute);
                        }
                        if (position == endHour) {
                            hour = hour.withEndMinutes(endMinute);
                        }
                        hours.set(
                            position,
                            hour.withColor(threshold.color())
                        );
                    });
            });

        notifyDataSetChanged();
    }

    private void initHours() {
        this.hours = IntStream
            .range(0, 24)
            .boxed()
            .map(hour ->
                Hour.builder()
                    .hour(hour)
                    .startMinutes(0)
                    .endMinutes(59)
                    .color(colorTransparent)
                    .build()
            )
            .collect(Collectors.toList());
    }
}
