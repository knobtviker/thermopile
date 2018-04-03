package com.knobtviker.thermopile.di.components.data;

import com.knobtviker.thermopile.di.modules.data.PeripheralsDataModule;
import com.knobtviker.thermopile.domain.repositories.PeripheralsRepository;

import dagger.Component;

/**
 * Created by bojan on 12/12/2017.
 */

@Component(modules = PeripheralsDataModule.class)
public interface PeripheralsDataComponent {

    PeripheralsRepository repository();
}
