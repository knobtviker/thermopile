package com.knobtviker.thermopile.di.components.data;

import com.knobtviker.thermopile.di.modules.data.ThresholdDataModule;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;

import dagger.Component;

/**
 * Created by bojan on 12/12/2017.
 */

@Component(modules = ThresholdDataModule.class)
public interface ThresholdDataComponent {

    ThresholdRepository repository();
}
