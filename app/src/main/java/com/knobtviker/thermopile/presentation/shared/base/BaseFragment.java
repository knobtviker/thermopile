package com.knobtviker.thermopile.presentation.shared.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;

/**
 * Created by bojan on 15/06/2017.
 */

//public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements BaseView {
public abstract class BaseFragment<P extends BasePresenter> extends DaggerFragment implements BaseView {

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
            presenter = null;
        }

        if (!unbinder.equals(Unbinder.EMPTY)) {
            unbinder.unbind();
            unbinder = Unbinder.EMPTY;
        }

        super.onDestroyView();
    }
}