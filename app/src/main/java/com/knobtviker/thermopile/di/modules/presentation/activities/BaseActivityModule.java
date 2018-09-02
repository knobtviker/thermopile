package com.knobtviker.thermopile.di.modules.presentation.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

//@Module
public abstract class BaseActivityModule {

//    @Provides
    static SharedPreferences providePreferences(@NonNull final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }
}
