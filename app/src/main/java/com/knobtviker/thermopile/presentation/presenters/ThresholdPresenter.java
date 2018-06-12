package com.knobtviker.thermopile.presentation.presenters;

import android.support.annotation.NonNull;

import com.knobtviker.thermopile.data.models.local.Threshold;
import com.knobtviker.thermopile.di.components.domain.repositories.DaggerThresholdRepositoryComponent;
import com.knobtviker.thermopile.di.modules.data.sources.local.ThresholdLocalDataSourceModule;
import com.knobtviker.thermopile.domain.repositories.ThresholdRepository;
import com.knobtviker.thermopile.presentation.contracts.ThresholdContract;
import com.knobtviker.thermopile.presentation.presenters.implementation.AbstractPresenter;

/**
 * Created by bojan on 29/10/2017.
 */

public class ThresholdPresenter extends AbstractPresenter implements ThresholdContract.Presenter {

    private final ThresholdContract.View view;

    private final ThresholdRepository thresholdRepository;

    public ThresholdPresenter(@NonNull final ThresholdContract.View view) {
        super(view);

        this.view = view;
        this.thresholdRepository = DaggerThresholdRepositoryComponent.builder()
            .localDataSource(new ThresholdLocalDataSourceModule())
            .build()
            .repository();
    }

    @Override
    public void loadById(long thresholdId) {
        started();

        compositeDisposable.add(
            thresholdRepository
                .loadById(thresholdId)
                .subscribe(
                    view::onThreshold,
                    this::error,
                    this::completed
                )
        );
    }

    @Override
    public void save(@NonNull Threshold threshold) {
        started();

        compositeDisposable.add(
            thresholdRepository
                .save(threshold)
                .subscribe(
                    resultId-> {
                        view.onSaved();
                    },
                    this::error,
                    this::completed
                )
        );
    }
}
