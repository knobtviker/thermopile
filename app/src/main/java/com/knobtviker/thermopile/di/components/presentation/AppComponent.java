package com.knobtviker.thermopile.di.components.presentation;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.modules.data.sources.LocalDataSourceModule;
import com.knobtviker.thermopile.di.modules.data.sources.RawDataSourceModule;
import com.knobtviker.thermopile.di.modules.domain.PreferencesModule;
import com.knobtviker.thermopile.di.modules.domain.RepositoriesModule;
import com.knobtviker.thermopile.di.modules.domain.SchedulersModule;
import com.knobtviker.thermopile.di.modules.presentation.ActivitiesModule;
import com.knobtviker.thermopile.di.modules.presentation.ApplicationModule;
import com.knobtviker.thermopile.di.modules.presentation.FragmentsModule;
import com.knobtviker.thermopile.presentation.ThermopileApplication;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
    AndroidSupportInjectionModule.class,
    ApplicationModule.class,
    ActivitiesModule.class,
    FragmentsModule.class,
    SchedulersModule.class,
    RepositoriesModule.class,
    LocalDataSourceModule.class,
    RawDataSourceModule.class,
    PreferencesModule.class
})
public interface AppComponent extends AndroidInjector<ThermopileApplication> {

    @Component.Builder
    interface Builder {

        Builder applicationModule(@NonNull final ApplicationModule applicationModule);

        AppComponent build();
    }
}
