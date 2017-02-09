package com.github.satoshun.reactive.unlife;

import com.github.satoshun.reactive.unlife.internal.OperatorUntilUnlife;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.Single;

/**
 * Continues a subscription until it sees *any* lifecycle event.
 */
final class UntilUnlifeObservableTransformer<T, R> implements UnLifeTransformer<T> {

  final Observable<R> lifecycle;

  UntilUnlifeObservableTransformer(@Nonnull Observable<R> lifecycle) {
    this.lifecycle = lifecycle;
  }

  @Override
  public Observable<T> call(Observable<T> source) {
    return source.lift(new OperatorUntilUnlife<T, R>(lifecycle));
  }

  @Nonnull
  @Override
  public Single.Transformer<T, T> forSingle() {
    return new UntilUnLifeSingleTransformer<>(lifecycle);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    UntilUnlifeObservableTransformer<?, ?> that = (UntilUnlifeObservableTransformer<?, ?>) o;

    return lifecycle.equals(that.lifecycle);
  }

  @Override
  public int hashCode() {
    return lifecycle.hashCode();
  }

  @Override
  public String toString() {
    return "UntilUnlifeObservableTransformer{" +
        "lifecycle=" + lifecycle +
        '}';
  }
}
