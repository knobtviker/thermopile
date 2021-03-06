package com.knobtviker.thermopile.presentation.views.adapters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.shared.SingleModel;
import com.knobtviker.thermopile.presentation.views.spark.SparkAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ChartAdapter<M extends SingleModel> extends SparkAdapter {

    private float baseline;

    private List<? extends SingleModel> data;

    @Inject
    public ChartAdapter() {
        this.data = new ArrayList<>(0);
        this.baseline = 0.0f;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @NonNull
    @Override
    public Object getItem(int index) {
        return data.get(index);
    }

    @Override
    public float getY(int index) {
        return data.get(index).value;
    }

    @Override
    public boolean hasBaseLine() {
        return true;
    }

    @Override
    public float getBaseLine() {
        return baseline;
    }

    public void setBaseline(final float value) {
        this.baseline = value;

        notifyDataSetChanged();
    }

    public void setData(@NonNull final List<? extends SingleModel> data) {
        this.data = data;

        notifyDataSetChanged();
    }
}
