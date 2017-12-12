package com.knobtviker.thermopile.di.components.data;

import com.knobtviker.thermopile.di.modules.data.SettingsDataModule;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;

import dagger.Component;

/**
 * Created by bojan on 12/12/2017.
 */

@Component(modules = SettingsDataModule.class)
public interface SettingsDataComponent {

    SettingsRepository repository();
}
