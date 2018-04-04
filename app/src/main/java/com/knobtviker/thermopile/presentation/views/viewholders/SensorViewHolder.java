package com.knobtviker.thermopile.presentation.views.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Switch;

import com.knobtviker.thermopile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bojan on 13/06/2017.
 */

public class SensorViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.switch_sensor_on_off)
    public Switch switchSensorOnOff;

    public SensorViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
