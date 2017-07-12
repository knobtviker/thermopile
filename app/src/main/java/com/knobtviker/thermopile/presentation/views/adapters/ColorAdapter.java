package com.knobtviker.thermopile.presentation.views.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.views.viewholders.ColorViewHolder;

/**
 * Created by bojan on 13/06/2017.
 */

public class ColorAdapter extends RecyclerView.Adapter<ColorViewHolder> {

    private final ImmutableList<Integer> colors;

    private final LayoutInflater layoutInflater;

    private final Button targetView;

    public ColorAdapter(@NonNull final Context context, @NonNull final Button targetView) {
        this.layoutInflater = LayoutInflater.from(context);
        this.targetView = targetView;

        this.colors = ImmutableList.of(
            ContextCompat.getColor(context, R.color.red),
            ContextCompat.getColor(context, R.color.blue)
        );
    }

    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ColorViewHolder(layoutInflater.inflate(R.layout.item_color, null));
    }

    @Override
    public void onBindViewHolder(ColorViewHolder colorViewHolder, int position) {
        final int color = colors.get(position);
        colorViewHolder.backgroundView.setBackgroundColor(color);
        colorViewHolder.backgroundView.setOnClickListener(view -> targetView.setBackgroundTintList(ColorStateList.valueOf(color)));
    }

    @Override
    public void onViewRecycled(ColorViewHolder colorViewHolder) {
        colorViewHolder.backgroundView.setOnClickListener(null);
        super.onViewRecycled(colorViewHolder);
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public int getItem(final int position) {
        return colors.get(position);
    }
}
