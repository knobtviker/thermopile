package com.knobtviker.thermopile.data.sources.raw.implementation.observers;

import android.support.annotation.NonNull;

import com.knobtviker.android.things.contrib.driver.tsl2561.TSL2561;

import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;

/**
 * Created by bojan on 19/07/2017.
 */

public class TSL2561Observer implements SingleSource<Float> {

    private final String bus;

    public static TSL2561Observer create(@NonNull final String bus) {
        return new TSL2561Observer(bus);
    }

    private TSL2561Observer(@NonNull final String bus) {
        this.bus = bus;
    }

    @Override
    public void subscribe(SingleObserver<? super Float> observer) {
        try {
            final TSL2561 tsl2561 = new TSL2561(bus);
            tsl2561.turnOn();
            tsl2561.setGain();
            tsl2561.setIntegration();
            tsl2561.setAutoGain(true);

            final float luminosity = tsl2561.readLuminosity();
            observer.onSuccess(luminosity);

            tsl2561.close();
        } catch (Exception e) {
//            observer.onError(e); //TODO: Uncomment this line once Android Things really closes GPIO. It is a known issue.
        }
    }
}
