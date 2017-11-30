package com.knobtviker.thermopile.presentation.fragments.implementation;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.knobtviker.thermopile.presentation.presenters.implementation.BasePresenter;

import java.util.Optional;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;

/**
 * Created by bojan on 15/06/2017.
 */

public abstract class BaseFragment<P extends BasePresenter> extends Fragment {

    protected P presenter;

    protected Realm realm = null;

    private Unbinder unbinder = Unbinder.EMPTY;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (presenter != null) {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
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
            if (realm != null) {
                if (!realm.isClosed()) {
                    realm.close();
                }
            }
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

            final Activity activity = getActivity();
            activity.setActionBar(toolbarInternal);

            final Optional<ActionBar> actionBarOptional = Optional.ofNullable(activity.getActionBar());
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