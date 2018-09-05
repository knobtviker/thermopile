package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.SettingsContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;

import javax.inject.Inject;

/**
 * Created by bojan on 15/07/2017.
 */

public class SettingsPresenter extends AbstractPresenter<SettingsContract.View> implements SettingsContract.Presenter {

    @Inject
    public SettingsPresenter(
        @NonNull final SettingsContract.View view,
        @NonNull final Schedulers schedulers
        ) {
        super(view, schedulers);
    }
}
