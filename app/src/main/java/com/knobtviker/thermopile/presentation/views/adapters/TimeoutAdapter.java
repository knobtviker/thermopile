package com.knobtviker.thermopile.presentation.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.utils.DateTimeKit;

import java.util.List;
import java.util.Locale;

public class TimeoutAdapter extends ArrayAdapter<Integer> {

    @NonNull
    private final List<Integer> items;

    public TimeoutAdapter(@NonNull final Context context, @NonNull final List<Integer> items) {
        super(context, R.layout.item_spinner, items);

        this.items = items;

        setDropDownViewResource(R.layout.item_spinner);
    }

    @NonNull
    @Override
    public Integer getItem(int position) {
        final Integer item = super.getItem(position);
        return item == null ? -1 : item;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner, parent, false);
        } else {
            view = convertView;
        }

        final Integer item = getItem(position);

        final TextView textView1 = view.findViewById(R.id.text1);

        textView1.setText(buildItemText(item));

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner, parent, false);
        } else {
            view = convertView;
        }

        final Integer item = getItem(position);

        final TextView textView1 = view.findViewById(R.id.text1);

        textView1.setText(buildItemText(item));

        return view;
    }

    @NonNull
    public List<Integer> getItems() {
        return items;
    }

    private String buildItemText(@NonNull final Integer item) {
        final long minutes = DateTimeKit.secondsToMinutes(item);
        if (minutes > 0) {
            return String.format(Locale.getDefault(), "%d min", minutes);
        } else {
            return String.format(Locale.getDefault(), "%d s", item);
        }
    }
}