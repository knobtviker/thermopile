package com.knobtviker.thermopile.data.sources.local;

import com.knobtviker.thermopile.data.models.local.Temperature;
import com.knobtviker.thermopile.data.models.local.Temperature_;
import com.knobtviker.thermopile.data.sources.local.implementation.AbstractLocalDataSource;

import java.util.List;

import javax.inject.Inject;

import io.objectbox.reactive.DataSubscription;
import io.reactivex.Observable;

/**
 * Created by bojan on 26/06/2017.
 */

public class TemperatureLocalDataSource extends AbstractLocalDataSource<Temperature> {

    @Inject
    public TemperatureLocalDataSource() {
        super(Temperature.class);
    }

    @Override
    public Observable<List<Temperature>> observe() {
        return super.observe(
            box.query()
                .order(Temperature_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<List<Temperature>> query() {
        return super.query(
            box.query()
                .order(Temperature_.timestamp)
                .build()
        );
    }

    @Override
    public Observable<Temperature> queryById(final long id) {
        return super.queryById(
            box.query()
                .equal(Temperature_.id, id)
                .build()
        );
    }

        public Observable<Temperature> observeLast() {
        return Observable.create(emitter -> {
            final DataSubscription dataSubscription =  box.query()
                .order(Temperature_.timestamp)
                .build()
                .subscribe()
                .onError(emitter::onError)
                .observer(data -> {
                if (!emitter.isDisposed()) {
                    emitter.onNext(data.get(0));
                }
            });
            emitter.setCancellable(dataSubscription::cancel);
        });
    }

//    private Box<Temperature2> box;

//    public Temperature2LocalDataSource() {
//        box = DatabaseBox.getInstance().boxFor(Temperature2.class);
//    }

//    public Observable<List<Temperature2>> observe() {
//        return Observable.create(emitter -> {
//            final DataSubscription dataSubscription =  box.query()
//                .order(Temperature_.timestamp)
//                .build()
//                .subscribe()
//                .onError(emitter::onError)
//                .observer(data -> {
//                if (!emitter.isDisposed()) {
//                    emitter.onNext(data);
//                }
//            });
//            emitter.setCancellable(dataSubscription::cancel);
//        });
//    }
//
//    public Observable<List<Temperature2>> observe() {
//        return Observable.create(emitter -> {
//            final DataSubscription dataSubscription =  box.query()
//                .order(Temperature_.timestamp)
//                .build()
//                .subscribe()
//                .onError(emitter::onError)
//                .observer(data -> {
//                    if (!emitter.isDisposed()) {
//                        emitter.onNext(data);
//                        emitter.onComplete();
//                    }
//                });
//            emitter.setCancellable(dataSubscription::cancel);
//        });
//    }
//
//    public Observable<Temperature2> loadById(final long id) {
//        return Observable.create(emitter -> {
//            if (!emitter.isDisposed()) {
//                final Temperature2 item = box.query()
//                    .equal(Temperature_.id, id)
//                    .build()
//                    .findFirst();
//
//                if (item != null) {
//                    emitter.onNext(item);
//                }else {
//                    emitter.onError(new Throwable(String.format("Item with Id %d not found", id)));
//                }
//                emitter.onComplete();
//            }
//        });
//    }
//
//    public Completable save(@NonNull final List<Temperature2> items) {
//        return Completable.create(emitter -> {
//            if (!emitter.isDisposed()) {
//                box.put(items);
//                emitter.onComplete();
//            }
//        });
//    }
//
//    public Observable<Long> save(@NonNull final Temperature2 item) {
//        return Observable.create(emitter -> {
//            if (!emitter.isDisposed()) {
//                final long id = box.put(item);
//                if (id > 0) {
//                    emitter.onNext(id);
//                } else {
//                    emitter.onError(new Throwable("Item Id cannot be 0"));
//                }
//            }
//        });
//    }
}
