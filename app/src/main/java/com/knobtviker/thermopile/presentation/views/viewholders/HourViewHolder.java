package com.knobtviker.thermopile.presentation.views.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.knobtviker.thermopile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bojan on 13/06/2017.
 */

public class HourViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textview_hour)
    public TextView textViewHour;

    public HourViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
