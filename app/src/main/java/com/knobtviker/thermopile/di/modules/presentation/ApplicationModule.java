package com.knobtviker.thermopile.di.modules.presentation;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ApplicationModule {

    @Binds
    abstract Context provideInstance(@NonNull final Application application);

//    @Provides
//    static SharedPreferences providePreferences(@NonNull final Context context) {
//        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
//    }
}
