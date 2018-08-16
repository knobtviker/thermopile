package com.knobtviker.thermopile.di.modules.presentation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.knobtviker.thermopile.di.qualifiers.presentation.ActivityScope;
import com.knobtviker.thermopile.di.qualifiers.presentation.ApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    @NonNull
    private final Context context;

    public ContextModule(@NonNull final Context context) {
        this.context = context;
    }

    @Provides
    @ApplicationScope
    Context provideApplicationContext() {
        return context.getApplicationContext();
    }

    @Provides
    @ActivityScope
    Context provideActivityContext() {
        return context;
    }
}
