package com.knobtviker.thermopile.domain.repositories;

import com.knobtviker.thermopile.domain.repositories.implementation.BaseRepository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Created by bojan on 17/07/2017.
 */

public class ClockRepository extends BaseRepository {

    private static Optional<ClockRepository> INSTANCE = Optional.empty();

    public static ClockRepository getInstance() {
        if (!INSTANCE.isPresent()) {
            INSTANCE = Optional.of(new ClockRepository());
        }

        return INSTANCE.get();
    }

    public static void destroyInstance() {
        if (INSTANCE.isPresent()) {
            INSTANCE = Optional.empty();
        }
    }

    private ClockRepository() {
    }

    public Observable<Long> get() {
        return Observable
            .interval(1L, TimeUnit.SECONDS, schedulerProvider.clock())
            .observeOn(schedulerProvider.ui());
    }
}
