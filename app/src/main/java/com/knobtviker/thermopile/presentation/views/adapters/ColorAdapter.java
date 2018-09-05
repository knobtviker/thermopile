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

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.utils.ColorKit;
import com.knobtviker.thermopile.presentation.views.viewholders.ColorViewHolder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by bojan on 13/06/2017.
 */

public class ColorAdapter extends RecyclerView.Adapter<ColorViewHolder> {

    private final List<String> colors;

    private final int radius;

    private String selectedColor;

    public ColorAdapter(@NonNull final Context context) {
        this.radius = context.getResources().getDimensionPixelSize(R.dimen.corner_24dp);

        final TypedArray colors500TypedArray = context.getResources().obtainTypedArray(R.array.colors_500);
        final String[] colors500 = new String[colors500TypedArray.length()];
        for (int i = 0; i < colors500TypedArray.length(); i++) {
            colors500[i] = ColorKit.colorToString(colors500TypedArray.getColor(i, 0));
        }
        colors500TypedArray.recycle();

        this.colors = Arrays.asList(colors500);
        this.selectedColor = colors.get(0);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new ColorViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_color, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder colorViewHolder, int position) {
        final int color = ColorKit.colorFromString(colors.get(position));

        final ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.setIntrinsicHeight(radius*2);
        shapeDrawable.setIntrinsicWidth(radius*2);
        shapeDrawable.getPaint().setColor(color);

        colorViewHolder.backgroundView.setBackground(shapeDrawable);
        colorViewHolder.imageViewSelected.setVisibility(selectedColor.equalsIgnoreCase(colors.get(position)) ? View.VISIBLE : View.GONE);
        colorViewHolder.backgroundView.setOnClickListener(view -> setSelectedColor(colors.get(position)));
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

    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(@NonNull final String color) {
        selectedColor = color;

        notifyDataSetChanged();
    }
}
