package com.example.chance.util;

import com.google.android.gms.tasks.Task;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public final class RxFirebase {
    private RxFirebase() {}

    public static <T> Single<T> toSingle(Task<T> task) {
        return Single.create(emitter -> {
            task
                .addOnSuccessListener(result -> {
                    if (!emitter.isDisposed()) {
                        emitter.onSuccess(result);
                    }
                })
                .addOnFailureListener(error -> {
                    if (!emitter.isDisposed()) {
                        emitter.onError(error);
                    }
                });
        });
    }

    public static Completable toCompletable(Task<Void> task) {
        return Completable.create(emitter -> {
            task
                .addOnSuccessListener(aVoid -> {
                    if (!emitter.isDisposed()) {
                        emitter.onComplete();
                    }
                })
                .addOnFailureListener(error -> {
                    if (!emitter.isDisposed()) {
                        emitter.onError(error);
                    }
                });
        });
    }
}
