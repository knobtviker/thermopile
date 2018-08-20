package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.presentation.contracts.WirelessContract;
import com.knobtviker.thermopile.presentation.shared.base.AbstractPresenter;

/**
 * Created by bojan on 15/07/2017.
 */

public class WirelessPresenter extends AbstractPresenter implements WirelessContract.Presenter {

    private final WirelessContract.View view;

    public WirelessPresenter(@NonNull final WirelessContract.View view) {
        super(view);

        this.view = view;
    }
}
