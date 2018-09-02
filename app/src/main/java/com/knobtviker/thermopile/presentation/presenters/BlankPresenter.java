package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.contracts.BlankContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;

/**
 * Created by bojan on 15/07/2017.
 */

public class BlankPresenter extends AbstractPresenter implements BlankContract.Presenter {

    public BlankPresenter(@NonNull final BlankContract.View view) {
        super(view);
    }
}
