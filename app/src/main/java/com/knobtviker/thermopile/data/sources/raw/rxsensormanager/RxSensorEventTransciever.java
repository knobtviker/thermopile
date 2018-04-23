package com.knobtviker.thermopile.data.sources.raw.rxsensormanager;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.FlowableEmitter;

public class RxSensorEventTransciever {

    @Nullable
    private FlowableEmitter<RxSensorEvent> emitter;

    @NonNull
    private SensorEventListener listener;

    public static RxSensorEventTransciever create() {
        return new RxSensorEventTransciever();
    }

    public void setEmitter(@NonNull final FlowableEmitter<RxSensorEvent> emitter) {
        this.emitter = emitter;
        this.listener = createListener();
    }

    public SensorEventListener listener() {
        return listener;
    }

    private RxSensorEventTransciever() {
    }

    private SensorEventListener createListener() {
        return new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (emitter != null && !emitter.isCancelled()) {
                    emitter.onNext(new RxSensorEvent(sensorEvent));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                if (emitter != null && !emitter.isCancelled()) {
                    emitter.onNext(new RxSensorEvent(sensor, accuracy));
                }
            }
        };
    }
}
