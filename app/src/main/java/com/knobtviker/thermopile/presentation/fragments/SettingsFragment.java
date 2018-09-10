package com.knobtviker.thermopile.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.knobtviker.thermopile.R;
import com.knobtviker.thermopile.presentation.contracts.SettingsContract;
import com.knobtviker.thermopile.presentation.shared.base.BaseFragment;

import java.util.Objects;

import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by bojan on 15/06/2017.
 */

public class SettingsFragment extends BaseFragment<SettingsContract.Presenter> implements SettingsContract.View {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.bottom_navigation_view)
    public BottomNavigationView bottomNavigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupBottomNavigationView();
    }

    @Override
    protected void load() {
        // no-op
    }

    @Override
    public void showLoading(boolean isLoading) {
        // no-op
    }

    @Override
    public void showError(@NonNull Throwable throwable) {
        Timber.e(throwable);

        Snackbar.make(Objects.requireNonNull(getView()), throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

    private void setupToolbar() {
        final NavController navController = NavHostFragment.findNavController(this);
        toolbar.inflateMenu(R.menu.settings);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.showAboutAction) {
                navController.popBackStack(); //TODO: Remove this temporary hack when Navigation BottomNavigationView bug is fixed.
            } else {
                NavigationUI.onNavDestinationSelected(menuItem, navController);
            }
            return false;
        });
        NavigationUI.setupWithNavController(toolbar, navController);
    }

    private void setupBottomNavigationView() {
        final NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.navigation_host_settings);
        if (navHostFragment != null) {
            final NavController navController = navHostFragment.getNavController();

            bottomNavigationView.setOnNavigationItemSelectedListener(item -> onNavDestinationSelected(item, navController));

            navController.addOnNavigatedListener((controller, destination) -> {
                final Menu menu = bottomNavigationView.getMenu();
                for (int h = 0, size = menu.size(); h < size; h++) {
                    final MenuItem item = menu.getItem(h);
                    if (matchDestination(destination, item.getItemId())) {
                        item.setChecked(true);
                    }
                }
            });
        } else {
            showError(new IllegalStateException("Cannot find NavHostFragment"));
        }
    }

    private boolean onNavDestinationSelected(@NonNull MenuItem item, @NonNull NavController navController) {
        final NavOptions options = new NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(findStartDestination(navController.getGraph()).getId(), false)
            .build();
        try {
            navController.navigate(item.getItemId(), null, options);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean matchDestination(@NonNull NavDestination destination, @IdRes int destId) {
        NavDestination currentDestination = destination;
        while (currentDestination.getId() != destId && currentDestination.getParent() != null) {
            currentDestination = currentDestination.getParent();
        }
        return currentDestination.getId() == destId;
    }

    private NavDestination findStartDestination(@NonNull NavGraph graph) {
        NavDestination startDestination = graph;
        while (startDestination instanceof NavGraph) {
            NavGraph parent = (NavGraph) startDestination;
            startDestination = parent.findNode(parent.getStartDestination());
        }
        return startDestination;
    }
}

//((ThermopileApplication) requireActivity().getApplication()).reset();
//                Timber.i("Version name: " + BuildConfig.VERSION_NAME + " Version code: " + BuildConfig.VERSION_CODE + " Hash: "
//                    + BuildConfig.GIT_COMMIT_SHA + " Build time: " + BuildConfig.GIT_COMMIT_TIMESTAMP + " Reboot count: "
//                    + ThermopileApplication.bootCount() + " Last boot timestamp: " + ThermopileApplication.lastBootTimestamp());

