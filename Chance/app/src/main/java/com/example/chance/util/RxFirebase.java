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

/**
 * ==================== RxFirebase.java Comments ====================
 *
 * This file contains the RxFirebase class, a final utility class that provides
 * helper methods to convert Google's Task API into RxJava 3 observables. This
 * allows for better composition of asynchronous operations and integration with
 * a reactive programming model.
 *
 * === RxFirebase Class ===
 * A final utility class with a private constructor to prevent instantiation. It serves
 * as a bridge between the callback-based Google Play Services Task API and the
 * reactive streams of RxJava.
 *
 * --- toSingle Method ---
 * Converts a `Task<T>` that is expected to return a result into a `Single<T>`.
 * A `Single` is an observable that emits either a single successful value or an error.
 * - If the Task is successful, the `onSuccess` of the Single emitter is called with the result.
 * - If the Task fails, the `onError` of the Single emitter is called with the exception.
 * This is ideal for Firebase operations that retrieve a single piece of data, such as
 * fetching a document.
 *
 * @param <T> The type of the result produced by the Task.
 * @param task The Google Play Services Task to convert.
 * @return A `Single<T>` that will emit the task's result or an error.
 *
 * --- toCompletable Method ---
 * Converts a `Task<Void>` that does not return a value into a `Completable`.
 * A `Completable` is an observable that only signals completion or an error, without
 * emitting a value.
 * - If the Task is successful, the `onComplete` of the Completable emitter is called.
 * - If the Task fails, the `onError` of the Completable emitter is called with the exception.
 * This is ideal for Firebase operations that perform an action, such as writing, updating,
 * or deleting a document.
 *
 * @param task The Google Play Services Task to convert.
 * @return A `Completable` that will signal completion or an error.
 */