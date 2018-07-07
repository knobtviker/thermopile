package com.knobtviker.thermopile.presentation.fragments.implementation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;
import com.knobtviker.thermopile.presentation.views.implementation.BaseView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by bojan on 15/06/2017.
 */

public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements BaseView {

    protected P presenter;

    private Unbinder unbinder = Unbinder.EMPTY;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        if (presenter != null) {
            presenter.dispose();
        }

        if (!unbinder.equals(Unbinder.EMPTY)) {
            unbinder.unbind();
        }

        super.onDestroyView();
    }
}