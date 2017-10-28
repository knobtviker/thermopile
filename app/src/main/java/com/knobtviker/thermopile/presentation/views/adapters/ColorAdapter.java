package com.knobtviker.thermopile.presentation.views.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
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

    private int selectedColor = 0;

    public ColorAdapter(@NonNull final Context context, @NonNull final Button targetView) {
        this.layoutInflater = LayoutInflater.from(context);
        this.targetView = targetView;

        final TypedArray colors500TypedArray = context.getResources().obtainTypedArray(R.array.colors_500);
        final Integer[] colors500 = new Integer[colors500TypedArray.length()];
        for (int i = 0; i < colors500TypedArray.length(); i++) {
            colors500[i] = colors500TypedArray.getColor(i, 0);
        }
        colors500TypedArray.recycle();

        this.colors = ImmutableList.copyOf(colors500);
        this.selectedColor = colors.get(0);
    }

    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ColorViewHolder(layoutInflater.inflate(R.layout.item_color, null));
    }

    @Override
    public void onBindViewHolder(ColorViewHolder colorViewHolder, int position) {
        final int color = colors.get(position);
        colorViewHolder.backgroundView.setBackgroundColor(color);
        colorViewHolder.backgroundView.setOnClickListener(view -> setSelectedColor(color));
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

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(final int color) {
        targetView.setBackgroundTintList(ColorStateList.valueOf(color));
        selectedColor = color;
    }
}
