package com.knobtviker.thermopile.di.components.presentation;

import android.app.Application;

import com.knobtviker.thermopile.di.modules.presentation.ActivitiesModule;
import com.knobtviker.thermopile.di.modules.presentation.ApplicationModule;
import com.knobtviker.thermopile.di.modules.presentation.FragmentsModule;
import com.knobtviker.thermopile.presentation.ThermopileApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import dagger.android.support.DaggerApplication;

@Singleton
@Component(modules = {
    AndroidSupportInjectionModule.class,
    ApplicationModule.class,
    ActivitiesModule.class,
    FragmentsModule.class
})
public interface AppComponent extends AndroidInjector<DaggerApplication> {

    void inject(ThermopileApplication application);

    @Override
    void inject(DaggerApplication instance);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
