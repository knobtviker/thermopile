package com.knobtviker.thermopile.presentation.fragments.implementation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by bojan on 15/06/2017.
 */

public abstract class BaseFragment<P extends BasePresenter> extends Fragment {

    protected P presenter;

    private Unbinder unbinder = Unbinder.EMPTY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (presenter != null) {
            presenter.subscribe();
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bind(this, view);
    }

    @Override
    public void onDestroyView() {
        if (!unbinder.equals(Unbinder.EMPTY)) {
            unbinder.unbind();
        }

        if (presenter != null) {
            presenter.unsubscribe();
        }

        super.onDestroyView();
    }

    protected void bind(@NonNull final Fragment fragment, @NonNull final View view) {
        unbinder = ButterKnife.bind(fragment, view);
    }
}