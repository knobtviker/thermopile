package com.knobtviker.thermopile.data.sources.raw.rxsensormanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;

public final class RxSensorManager {

    @SuppressLint("StaticFieldLeak")
    private static RxSensorManager INSTANCE = null;

    private SensorManager sensorManager;

    public static synchronized RxSensorManager getInstance(@NonNull final Context context) {
        if (INSTANCE == null) {
            INSTANCE = new RxSensorManager(context.getApplicationContext());
        }
        return INSTANCE;
    }

    /**
     * Creates RxSensorManager object
     *
     * @param context context
     */
    private RxSensorManager(@NonNull final Context context) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    /**
     * Gets lists of all sensors available on device
     *
     * @return list of sensors
     */
    public List<Sensor> getSensors() {
        return sensorManager.getDynamicSensorList(Sensor.TYPE_ALL);
    }

    /**
     * Checks if device has given sensor available
     *
     * @param sensorType from Sensor class available in Android SDK
     * @return boolean returns true if sensor is available
     */
    public boolean hasSensor(int sensorType) {
        return sensorManager.getDefaultSensor(sensorType) != null;
    }

    /**
     * Returns RxJava Observable, which allows to monitor hardware sensors
     * as a stream of ReactiveSensorEvent object.
     * Sampling period is set to SensorManager.SENSOR_DELAY_NORMAL.
     *
     * @return RxJava Observable with ReactiveSensorEvent
     */
    public Flowable<RxSensorEvent> observeSensors() {
        return observeSensors(SensorManager.SENSOR_DELAY_UI, null);
    }

    /**
     * Returns RxJava Observable, which allows to monitor hardware sensors
     * as a stream of ReactiveSensorEvent object with defined sampling period
     *
     * @param samplingPeriodInUs sampling period in microseconds,
     *                           you can use predefined values from SensorManager class with prefix SENSOR_DELAY
     * @return RxJava Observable with ReactiveSensorEvent
     */
    public Flowable<RxSensorEvent> observeSensors(final int samplingPeriodInUs) {
        return observeSensors(samplingPeriodInUs, null);
    }

    public Flowable<RxSensorEvent> observeSensors(final int samplingPeriodInUs, @Nullable final Handler handler) {
        return observeSensors(samplingPeriodInUs, handler, BackpressureStrategy.BUFFER);
    }

    /**
     * Returns RxJava Observable, which allows to monitor hardware sensors
     * as a stream of ReactiveSensorEvent object with defined sampling period
     *
     * @param samplingPeriodInUs sampling period in microseconds,
     * @param handler            the Handler the sensor events will be delivered to, use default if it is null
     *                           you can use predefined values from SensorManager class with prefix SENSOR_DELAY
     * @param strategy           BackpressureStrategy for RxJava 2 Flowable type
     * @return RxJava Observable with ReactiveSensorEvent
     */
    public Flowable<RxSensorEvent> observeSensors(final int samplingPeriodInUs, @Nullable final Handler handler, final BackpressureStrategy strategy) {
        final RxSensorEventTransceiver transceiver = RxSensorEventTransceiver.create();

        final SensorManager.DynamicSensorCallback callback = new SensorManager.DynamicSensorCallback() {
            @Override
            public void onDynamicSensorConnected(Sensor sensor) {
                if (handler == null) {
                    sensorManager.registerListener(transceiver.listener(), sensor, samplingPeriodInUs);
                } else {
                    sensorManager.registerListener(transceiver.listener(), sensor, samplingPeriodInUs, handler);
                }
            }

            @Override
            public void onDynamicSensorDisconnected(Sensor sensor) {
                sensorManager.unregisterListener(transceiver.listener());
            }
        };

        return Flowable.create((FlowableEmitter<RxSensorEvent> emitter) -> {
                transceiver.setEmitter(emitter);

                sensorManager.registerDynamicSensorCallback(callback);
            },
            strategy
        )
            .doOnCancel(() -> sensorManager.unregisterDynamicSensorCallback(callback));
    }

    public Flowable<RxSensorEvent> observeSensorsByType(final int sensorType) {
        return observeSensors(sensorType, SensorManager.SENSOR_DELAY_UI, null);
    }

    public Flowable<RxSensorEvent> observeSensors(final int sensorType, final int samplingPeriodInUs) {
        return observeSensors(sensorType, samplingPeriodInUs, null);
    }

    public Flowable<RxSensorEvent> observeSensors(final int sensorType, final int samplingPeriodInUs, @Nullable final Handler handler) {
        return observeSensors(sensorType, samplingPeriodInUs, handler, BackpressureStrategy.BUFFER);
    }

    public Flowable<RxSensorEvent> observeSensors(final int sensorType, final int samplingPeriodInUs, @Nullable final Handler handler, final BackpressureStrategy strategy) {
        final RxSensorEventTransceiver transceiver = RxSensorEventTransceiver.create();

        final SensorManager.DynamicSensorCallback callback = new SensorManager.DynamicSensorCallback() {
            @Override
            public void onDynamicSensorConnected(Sensor sensor) {
                if (sensor.getType() == sensorType) {
                    if (handler == null) {
                        sensorManager.registerListener(transceiver.listener(), sensor, samplingPeriodInUs);
                    } else {
                        sensorManager.registerListener(transceiver.listener(), sensor, samplingPeriodInUs, handler);
                    }
                }
            }

            @Override
            public void onDynamicSensorDisconnected(Sensor sensor) {
                if (sensor.getType() == sensorType) {
                    sensorManager.unregisterListener(transceiver.listener());
                }
            }
        };

        return Flowable.create((FlowableEmitter<RxSensorEvent> emitter) -> {
                transceiver.setEmitter(emitter);
                sensorManager.registerDynamicSensorCallback(callback);
            },
            strategy
        )
            .doOnCancel(() -> sensorManager.unregisterDynamicSensorCallback(callback));
    }
}