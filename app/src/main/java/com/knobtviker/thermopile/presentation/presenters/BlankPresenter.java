package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.domain.schedulers.Schedulers;
import com.knobtviker.thermopile.presentation.contracts.BlankContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;

import javax.inject.Inject;

/**
 * Created by bojan on 15/07/2017.
 */

public class BlankPresenter extends AbstractPresenter<BlankContract.View> implements BlankContract.Presenter {

    @Inject
    public BlankPresenter(
        @NonNull final BlankContract.View view,
        @NonNull final Schedulers schedulers
    ) {
        super(view, schedulers);
        this.view = view;
    }
}
