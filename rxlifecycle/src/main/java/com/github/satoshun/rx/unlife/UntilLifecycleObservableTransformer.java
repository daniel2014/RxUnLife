package com.github.satoshun.rx.unlife;

import rx.Completable;
import rx.Observable;
import rx.Single;

import javax.annotation.Nonnull;

/**
 * Continues a subscription until it sees *any* lifecycle event.
 */
final class UntilLifecycleObservableTransformer<T, R> implements LifecycleTransformer<T> {

    final Observable<R> lifecycle;

    public UntilLifecycleObservableTransformer(@Nonnull Observable<R> lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public Observable<T> call(Observable<T> source) {
        return source.takeUntil(lifecycle);
    }

    @Nonnull
    @Override
    public Single.Transformer<T, T> forSingle() {
        return new com.github.satoshun.rx.unlife.UntilLifecycleSingleTransformer<>(lifecycle);
    }

    @Nonnull
    @Override
    public Completable.Transformer forCompletable() {
        return new com.github.satoshun.rx.unlife.UntilLifecycleCompletableTransformer<>(lifecycle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        UntilLifecycleObservableTransformer<?, ?> that = (UntilLifecycleObservableTransformer<?, ?>) o;

        return lifecycle.equals(that.lifecycle);
    }

    @Override
    public int hashCode() {
        return lifecycle.hashCode();
    }

    @Override
    public String toString() {
        return "UntilLifecycleObservableTransformer{" +
            "lifecycle=" + lifecycle +
            '}';
    }
}
