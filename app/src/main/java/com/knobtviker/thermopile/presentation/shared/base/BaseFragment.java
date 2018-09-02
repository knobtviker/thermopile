package com.knobtviker.thermopile.presentation.shared.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;

/**
 * Created by bojan on 15/06/2017.
 */

public abstract class BaseFragment<P extends BasePresenter> extends DaggerFragment implements BaseView {

    @Inject
    protected P presenter;

//    @Inject
//    protected Unbinder unbinder;

    private Unbinder unbinder = Unbinder.EMPTY;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();

        load();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (presenter != null) {
            presenter.dispose();
        }
    }

    @Override
    public void onDestroyView() {
        if (!unbinder.equals(Unbinder.EMPTY)) {
            unbinder.unbind();
            unbinder = Unbinder.EMPTY;
        }

        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        if (presenter != null) {
            presenter = null;
        }

        super.onDetach();
    }

    protected abstract void load();
}