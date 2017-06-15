package com.knobtviker.thermopile.presentation.fragments.implementation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.Optional;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by bojan on 15/06/2017.
 */

public abstract class BaseFragment extends Fragment {

    public Unbinder unbinder;

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    protected void bind(@NonNull final Object object, @NonNull final View view) {
        unbinder = ButterKnife.bind(object, view);
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
            toolbarInternal.setContentInsetsAbsolute(0, 0); //Neverfixable hack

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
}
