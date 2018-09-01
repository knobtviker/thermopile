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

import java.util.ArrayList;
import java.util.Set;

public class TimezoneAdapter extends ArrayAdapter<String> {

    public TimezoneAdapter(@NonNull final Context context, @NonNull final Set<String> items) {
        super(context, R.layout.item_spinner, new ArrayList<>(items));

        setDropDownViewResource(R.layout.item_spinner);
    }

    @NonNull
    @Override
    public String getItem(int position) {
        final String item = super.getItem(position);
        return item == null ? "" : item;
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

        final String item = getItem(position);

        final TextView textView1 = view.findViewById(R.id.text1);

        textView1.setText(item);

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

        final String item = getItem(position);

        final TextView textView1 = view.findViewById(R.id.text1);

        textView1.setText(item);

        return view;
    }
}