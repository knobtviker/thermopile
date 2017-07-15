package com.knobtviker.thermopile.presentation.fragments.implementation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;

import java.util.Optional;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (presenter != null) {
            presenter.subscribe();
        }

        return super.onCreateView(inflater, container, savedInstanceState);
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

    protected void setupCustomActionBar(@Nullable Toolbar toolbar) {
        setupActionBar(toolbar, false);
    }

    protected void setupCustomActionBarWithHomeAsUp(@Nullable Toolbar toolbar) {
        setupActionBar(toolbar, true);
    }

    private void setupActionBar(@Nullable final Toolbar toolbar, final boolean homeAsUp) {
        final Optional<Toolbar> toolbarOptional = Optional.ofNullable(toolbar);
        if (toolbarOptional.isPresent()) {
            final Toolbar toolbarInternal = toolbarOptional.get();

            final AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            appCompatActivity.setSupportActionBar(toolbarInternal);

            final Optional<ActionBar> actionBarOptional = Optional.ofNullable(appCompatActivity.getSupportActionBar());
            if (actionBarOptional.isPresent()) {
                final ActionBar actionBar = actionBarOptional.get();
                actionBar.setDisplayHomeAsUpEnabled(homeAsUp);
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setDisplayShowCustomEnabled(true);
            }
        }
    }

    protected void bind(@NonNull final Fragment fragment, @NonNull final View view) {
        unbinder = ButterKnife.bind(fragment, view);
    }
}