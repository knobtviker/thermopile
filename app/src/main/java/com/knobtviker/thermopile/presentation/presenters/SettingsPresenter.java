package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.contracts.SettingsContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;

/**
 * Created by bojan on 15/07/2017.
 */

public class SettingsPresenter extends AbstractPresenter implements SettingsContract.Presenter {

    public SettingsPresenter(@NonNull final SettingsContract.View view) {
        super(view);
    }
}
