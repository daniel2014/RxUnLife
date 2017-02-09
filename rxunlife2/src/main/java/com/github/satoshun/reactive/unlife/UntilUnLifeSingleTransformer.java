package com.github.satoshun.reactive.unlife;

import com.github.satoshun.reactive.unlife.internal.SingleUnlifeObservable;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.Single;

/**
 * Continues a subscription until it sees *any* lifecycle event.
 */
final class UntilUnLifeSingleTransformer<T, R> implements Single.Transformer<T, T> {

  final Observable<R> lifecycle;

  UntilUnLifeSingleTransformer(@Nonnull Observable<R> lifecycle) {
    this.lifecycle = lifecycle;
  }

  @Override
  public Single<T> call(Single<T> source) {
    return Single.create(new SingleUnlifeObservable<>(source, lifecycle));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    UntilUnLifeSingleTransformer<?, ?> that = (UntilUnLifeSingleTransformer<?, ?>) o;

    return lifecycle.equals(that.lifecycle);
  }

  @Override
  public int hashCode() {
    return lifecycle.hashCode();
  }

  @Override
  public String toString() {
    return "UntilUnLifeSingleTransformer{" +
        "lifecycle=" + lifecycle +
        '}';
  }
}
