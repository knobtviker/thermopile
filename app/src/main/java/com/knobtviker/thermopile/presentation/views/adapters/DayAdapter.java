package com.knobtviker.thermopile.presentation.views.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.google.common.collect.ImmutableList;
import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.views.viewholders.DayViewHolder;

/**
 * Created by bojan on 30/10/2017.
 */

public class DayAdapter implements ListAdapter {

    private final ImmutableList<String> DAYS = ImmutableList.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

    private final Context context;

    public static DayAdapter create(@NonNull final Context context) {
        return new DayAdapter(context);
    }

    private DayAdapter(@NonNull final Context context) {
        this.context = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return DAYS.size();
    }

    @Override
    public String getItem(int i) {
        return DAYS.get(i);
    }

    @Override
    public long getItemId(int i) {
        return DAYS.get(i).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DayViewHolder holder;
        if (view == null) {
            holder = new DayViewHolder(LayoutInflater.from(context).inflate(R.layout.item_day, null));
            view.setTag(holder);
        } else {
            holder = (DayViewHolder) view.getTag();
        }

        final String day = getItem(i);

        holder.textViewTitle.setText(day);

        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
