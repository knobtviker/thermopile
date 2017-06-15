package com.knobtviker.thermopile.presentation.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.annimon.stream.IntStream;
import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.presentation.Mode;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.views.viewholders.HourViewHolder;

/**
 * Created by bojan on 13/06/2017.
 */

public class ModesAdapter extends RecyclerView.Adapter<HourViewHolder> {

    private final ImmutableList<Mode> modes;

    private final LayoutInflater layoutInflater;

    public ModesAdapter(@NonNull final Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.modes = ImmutableList.of(
            Mode.builder()
                .id(0L)
                .name("Work")
                .color(Color.GREEN)
                .lastModified(2L)
                .startHour(7)
                .startMinute(0)
                .endHour(18)
                .endMinute(0)
                .days(ImmutableList.of(Constants.DAYS_MONDAY, Constants.DAYS_TUESDAY, Constants.DAYS_WEDNESDAY, Constants.DAYS_THURSDAY, Constants.DAYS_FRIDAY))
                .temperature(15.0f)
                .build(),
            Mode.builder()
                .id(1L)
                .name("Weekend")
                .color(Color.RED)
                .lastModified(3L)
                .startHour(7)
                .startMinute(0)
                .endHour(23)
                .endMinute(0)
                .days(ImmutableList.of(Constants.DAYS_SATURDAY, Constants.DAYS_SUNDAY))
                .temperature(22.5f)
                .build(),
            Mode.builder()
                .id(0L)
                .name("Night")
                .color(Color.YELLOW)
                .lastModified(4L)
                .startHour(21)
                .startMinute(0)
                .endHour(0)
                .endMinute(0)
                .days(ImmutableList.of(Constants.DAYS_MONDAY, Constants.DAYS_TUESDAY, Constants.DAYS_WEDNESDAY, Constants.DAYS_THURSDAY, Constants.DAYS_FRIDAY, Constants.DAYS_SATURDAY, Constants.DAYS_SUNDAY))
                .temperature(18.5f)
                .build()
        );
    }

    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new HourViewHolder(layoutInflater.inflate(R.layout.item_mode, null));
    }

    @Override
    public void onBindViewHolder(HourViewHolder hourViewHolder, int position) {
        final Mode mode = modes.get(position);
    }

    @Override
    public int getItemCount() {
        return modes.size();
    }
}
