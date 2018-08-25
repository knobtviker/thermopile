package com.knobtviker.thermopile.presentation.views.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.knobtviker.thermopile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bojan on 25/08/2018.
 */

public class WirelessViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.imageview_icon)
    public ImageView imageViewIcon;

    @BindView(R.id.textview_ssid)
    public TextView textViewSSID;

    public WirelessViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
