package com.knobtviker.thermopile.di.components.domain.repositories;

import com.knobtviker.thermopile.di.modules.domain.repositories.ThresholdRepositoryModule;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;

import dagger.Component;

/**
 * Created by bojan on 12/12/2017.
 */

@Component(modules = ThresholdRepositoryModule.class)
public interface ThresholdRepositoryComponent {

    ThresholdRepository repository();
}
