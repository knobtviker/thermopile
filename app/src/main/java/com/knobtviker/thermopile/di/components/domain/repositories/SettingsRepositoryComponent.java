package com.knobtviker.thermopile.di.components.domain.repositories;

import com.knobtviker.thermopile.di.modules.domain.repositories.SettingsRepositoryModule;
import com.knobtviker.thermopile.domain.repositories.SettingsRepository;

import dagger.Component;

/**
 * Created by bojan on 12/12/2017.
 */

@Component(modules = SettingsRepositoryModule.class)
public interface SettingsRepositoryComponent {

    SettingsRepository repository();
}
