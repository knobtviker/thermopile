package com.knobtviker.thermopile.di.modules.data.sources.memory;

import com.knobtviker.thermopile.data.sources.local.AccelerationLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.AirQualityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.AltitudeLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.AngularVelocityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.HumidityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.LuminosityLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.MagneticFieldLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.PressureLocalDataSource;
import com.knobtviker.thermopile.data.sources.local.TemperatureLocalDataSource;
import com.knobtviker.thermopile.data.sources.memory.AccelerationMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.AirQualityMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.AltitudeMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.AngularVelocityMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.HumidityMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.LuminosityMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.MagneticFieldMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.PressureMemoryDataSource;
import com.knobtviker.thermopile.data.sources.memory.TemperatureMemoryDataSource;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bojan on 12/12/2017.
 */

@Module
public class AtmosphereMemoryDataSourceModule {

    @Provides
    static TemperatureMemoryDataSource provideTemperatureMemoryDataSource() {
        return new TemperatureMemoryDataSource();
    }

    @Provides
    static HumidityMemoryDataSource provideHumidityMemoryDataSource() {
        return new HumidityMemoryDataSource();
    }

    @Provides
    static PressureMemoryDataSource providePressureMemoryDataSource() {
        return new PressureMemoryDataSource();
    }

    @Provides
    static AltitudeMemoryDataSource provideAltitudeMemoryDataSource() {
        return new AltitudeMemoryDataSource();
    }

    @Provides
    static AirQualityMemoryDataSource provideAirQualityMemoryDataSource() {
        return new AirQualityMemoryDataSource();
    }

    @Provides
    static LuminosityMemoryDataSource provideLuminosityMemoryDataSource() {
        return new LuminosityMemoryDataSource();
    }

    @Provides
    static AccelerationMemoryDataSource provideAccelerationMemoryDataSource() {
        return new AccelerationMemoryDataSource();
    }

    @Provides
    static AngularVelocityMemoryDataSource provideAngularVelocityMemoryDataSource() {
        return new AngularVelocityMemoryDataSource();
    }

    @Provides
    static MagneticFieldMemoryDataSource provideMagneticFieldMemoryDataSource() {
        return new MagneticFieldMemoryDataSource();
    }
}
