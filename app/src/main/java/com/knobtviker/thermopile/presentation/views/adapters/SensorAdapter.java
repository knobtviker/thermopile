package com.knobtviker.thermopile.presentation.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.data.models.local.PeripheralDevice;
import com.knobtviker.thermopile.presentation.utils.Constants;
import com.knobtviker.thermopile.presentation.views.communicators.SensorCommunicator;
import com.knobtviker.thermopile.presentation.views.viewholders.SensorViewHolder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by bojan on 13/06/2017.
 */

public class SensorAdapter extends RecyclerView.Adapter<SensorViewHolder> {

    private final int type;

    private final LayoutInflater layoutInflater;

    private final SensorCommunicator sensorCommunicator;

    private List<PeripheralDevice> items = Collections.emptyList();

    public SensorAdapter(@NonNull final Context context, final int type, @NonNull SensorCommunicator sensorCommunicator) {
        this.type = type;
        this.layoutInflater = LayoutInflater.from(context);
        this.sensorCommunicator = sensorCommunicator;

        setHasStableIds(true);
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new SensorViewHolder(layoutInflater.inflate(R.layout.item_sensor, null));
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        final PeripheralDevice peripheralDevice = items.get(position);

        holder.switchSensorOnOff.setText(String.format("%s %s", peripheralDevice.vendor(), peripheralDevice.name()));
        switch (type) {
            case Constants.TYPE_TEMPERATURE:
                holder.switchSensorOnOff.setChecked(peripheralDevice.enabledTemperature());
                break;
            case Constants.TYPE_PRESSURE:
                holder.switchSensorOnOff.setChecked(peripheralDevice.enabledPressure());
                break;
            case Constants.TYPE_HUMIDITY:
                holder.switchSensorOnOff.setChecked(peripheralDevice.enabledHumidity());
                break;
            case Constants.TYPE_AIR_QUALITY:
                holder.switchSensorOnOff.setChecked(peripheralDevice.enabledAirQuality());
                break;
            case Constants.TYPE_LUMINOSITY:
                holder.switchSensorOnOff.setChecked(peripheralDevice.enabledLuminosity());
                break;
            case Constants.TYPE_ACCELERATION:
                holder.switchSensorOnOff.setChecked(peripheralDevice.enabledAcceleration());
                break;
            case Constants.TYPE_ANGULAR_VELOCITY:
                holder.switchSensorOnOff.setChecked(peripheralDevice.enabledAngularVelocity());
                break;
            case Constants.TYPE_MAGNETIC_FIELD:
                holder.switchSensorOnOff.setChecked(peripheralDevice.enabledMagneticField());
                break;
        }
        holder.switchSensorOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> sensorCommunicator.onSensorChecked(peripheralDevice.primaryKey(), type, isChecked));
        holder.switchSensorOnOff.setEnabled(peripheralDevice.connected());
    }

    @Override
    public void onViewRecycled(@NonNull SensorViewHolder holder) {
        holder.switchSensorOnOff.setOnCheckedChangeListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return Objects.hash(items.get(position).primaryKey());
    }

    public void updateData(List<PeripheralDevice> peripheralDevices) {
        this.items = peripheralDevices;
        notifyDataSetChanged();
    }
}
