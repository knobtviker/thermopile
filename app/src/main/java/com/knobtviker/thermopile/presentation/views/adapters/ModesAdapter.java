package com.knobtviker.thermopile.presentation.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.annimon.stream.IntStream;
import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.presentation.Mode;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.views.viewholders.ModeViewHolder;

/**
 * Created by bojan on 13/06/2017.
 */

public class ModesAdapter extends RecyclerView.Adapter<ModeViewHolder> {

    private final ImmutableList<Mode> modes;

    private final LayoutInflater layoutInflater;

    public ModesAdapter(@NonNull final Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.modes = ImmutableList.of(
            Mode.builder()
                .id(0L)
                .name("Work")
                .color(context.getColor(R.color.colorAccent))
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
                .color(context.getColor(R.color.colorPrimary))
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
                .color(context.getColor(R.color.colorPrimaryDark))
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
    public ModeViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ModeViewHolder(layoutInflater.inflate(R.layout.item_mode, null));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ModeViewHolder modeViewHolder, int position) {
        final Mode mode = modes.get(position);

        modeViewHolder.viewColorIndicator.setBackgroundColor(mode.color());

        modeViewHolder.textViewName.setText(mode.name());
        modeViewHolder.textViewStart.setText(String.format("%d:%d", mode.startHour(), mode.startMinute()));
        modeViewHolder.textViewEnd.setText(String.format("%d:%d", mode.endHour(), mode.endMinute()));

        modeViewHolder.textViewDaysMonday.setTextColor(mode.days().contains(Constants.DAYS_MONDAY) ? mode.color() : Color.WHITE);
        modeViewHolder.textViewDaysTuesday.setTextColor(mode.days().contains(Constants.DAYS_TUESDAY) ? mode.color() : Color.WHITE);
        modeViewHolder.textViewDaysWednesday.setTextColor(mode.days().contains(Constants.DAYS_WEDNESDAY) ? mode.color() : Color.WHITE);
        modeViewHolder.textViewDaysThursday.setTextColor(mode.days().contains(Constants.DAYS_THURSDAY) ? mode.color() : Color.WHITE);
        modeViewHolder.textViewDaysFriday.setTextColor(mode.days().contains(Constants.DAYS_FRIDAY) ? mode.color() : Color.WHITE);
        modeViewHolder.textViewDaysSaturday.setTextColor(mode.days().contains(Constants.DAYS_SATURDAY) ? mode.color() : Color.WHITE);
        modeViewHolder.textViewDaysSunday.setTextColor(mode.days().contains(Constants.DAYS_SUNDAY) ? mode.color() : Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return modes.size();
    }
}
