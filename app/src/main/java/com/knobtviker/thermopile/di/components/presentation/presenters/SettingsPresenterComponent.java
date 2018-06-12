package com.knobtviker.thermopile.di.components.presentation.presenters;

import com.knobtviker.thermopile.di.modules.data.sources.local.SettingsLocalDataSourceModule;
import com.knobtviker.thermopile.presentation.fragments.SettingsFragment;

import dagger.Component;

@Component(modules = {SettingsLocalDataSourceModule.class})
public interface SettingsPresenterComponent {

    void inject(SettingsFragment fragment);
}
