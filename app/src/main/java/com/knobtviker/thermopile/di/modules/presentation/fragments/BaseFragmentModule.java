package com.knobtviker.thermopile.di.modules.presentation.fragments;

import butterknife.Unbinder;

//@Module
public class BaseFragmentModule {

//    @Provides
    Unbinder provideUnbinder() {
        return Unbinder.EMPTY;
    }
}
