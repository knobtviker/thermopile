package com.knobtviker.thermopile.presentation.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.views.viewholders.ColorViewHolder;

/**
 * Created by bojan on 13/06/2017.
 */

public class ColorAdapter extends RecyclerView.Adapter<ColorViewHolder> {

    private final ImmutableList<Integer> colors;

    private final LayoutInflater layoutInflater;

    private final int radius;

    private int selectedColor;

    public ColorAdapter(@NonNull final Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.radius = context.getResources().getDimensionPixelSize(R.dimen.corner_24dp);

        final TypedArray colors500TypedArray = context.getResources().obtainTypedArray(R.array.colors_500);
        final Integer[] colors500 = new Integer[colors500TypedArray.length()];
        for (int i = 0; i < colors500TypedArray.length(); i++) {
            colors500[i] = colors500TypedArray.getColor(i, 0);
        }
        colors500TypedArray.recycle();

        this.colors = ImmutableList.copyOf(colors500);
        this.selectedColor = colors.get(0);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new ColorViewHolder(layoutInflater.inflate(R.layout.item_color, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder colorViewHolder, int position) {
        final int color = colors.get(position);

        final ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.setIntrinsicHeight(radius*2);
        shapeDrawable.setIntrinsicWidth(radius*2);
        shapeDrawable.getPaint().setColor(color);

        colorViewHolder.backgroundView.setBackground(shapeDrawable);
        colorViewHolder.imageViewSelected.setVisibility(selectedColor == color ? View.VISIBLE : View.GONE);
        colorViewHolder.backgroundView.setOnClickListener(view -> setSelectedColor(color));
    }

    @Override
    public void onViewRecycled(@NonNull ColorViewHolder colorViewHolder) {
        colorViewHolder.backgroundView.setOnClickListener(null);
        colorViewHolder.imageViewSelected.setVisibility(View.GONE);
        super.onViewRecycled(colorViewHolder);
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

//    public int getItem(final int position) {
//        return colors.get(position);
//    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(final int color) {
        selectedColor = color;

        notifyDataSetChanged();
    }
}
