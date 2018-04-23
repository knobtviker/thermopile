package com.knobtviker.thermopile.data.sources.raw.rxsensormanager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;

public final class RxSensorManager {

    private SensorManager sensorManager;

    private RxSensorManager() {
    }

    /**
     * Creates ReactiveSensors object
     *
     * @param context context
     */
    public RxSensorManager(Context context) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    /**
     * Gets lists of all sensors available on device
     *
     * @return list of sensors
     */
    public List<Sensor> getSensors() {
        return sensorManager.getSensorList(Sensor.TYPE_ALL);
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
        return observeSensors(SensorManager.SENSOR_DELAY_NORMAL, null);
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
//        if (!hasSensor(sensorType)) {
//            final String format = "Sensor with id = %d is not available on this device. Did you call registerDynamicSensorCallback for this sensor?";
//            final String message = String.format(Locale.getDefault(), format, sensorType);
//            return Flowable.error(new Exception(message));
//        }

        final RxSensorEventTransciever transciever = RxSensorEventTransciever.create();

        final SensorManager.DynamicSensorCallback callback = new SensorManager.DynamicSensorCallback() {
            @Override
            public void onDynamicSensorConnected(Sensor sensor) {
                if (handler == null) {
                    sensorManager.registerListener(transciever.listener(), sensor, samplingPeriodInUs);
                } else {
                    sensorManager.registerListener(transciever.listener(), sensor, samplingPeriodInUs, handler);
                }
            }

            @Override
            public void onDynamicSensorDisconnected(Sensor sensor) {
                sensorManager.unregisterListener(transciever.listener());
//                if (handler != null) {
//                    handler = null;
//                }
            }
        };

        return Flowable.create((FlowableEmitter<RxSensorEvent> emitter) -> {
                transciever.setEmitter(emitter);

                sensorManager.registerDynamicSensorCallback(callback);
            },
            strategy
        )
            .doOnCancel(() -> {
                sensorManager.unregisterDynamicSensorCallback(callback);
            });
    }
}