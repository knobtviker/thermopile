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

import org.joda.time.DateTime;

import java.util.List;

public class FormatAdapter extends ArrayAdapter<String> {

    public FormatAdapter(@NonNull final Context context, @NonNull final List<String> items) {
        super(context, R.layout.item_spinner_two_line, items);

        setDropDownViewResource(R.layout.item_spinner_two_line);
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_two_line, parent, false);
        } else {
            view = convertView;
        }

        final String item = getItem(position);

        final TextView textView1 = view.findViewById(R.id.text1);
        final TextView textView2 = view.findViewById(R.id.text2);

        textView1.setText(item);
        textView2.setText(DateTime.now().toString(item));

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_two_line, parent, false);
        } else {
            view = convertView;
        }

        final String item = getItem(position);

        final TextView textView1 = view.findViewById(R.id.text1);
        final TextView textView2 = view.findViewById(R.id.text2);

        textView1.setText(item);
        textView2.setText(DateTime.now().toString(item));

        return view;
    }
}