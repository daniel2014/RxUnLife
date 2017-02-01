package com.github.satoshun.rx.unlife;

import javax.annotation.Nonnull;

import rx.Completable;
import rx.Observable;
import rx.Single;

import static com.github.satoshun.rx.unlife.TakeUntilGenerator.takeUnlifeEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 */
final class UntilUnlifeObservableTransformer<T, R> implements UnLifeTransformer<T> {

  final Observable<R> lifecycle;
  final R event;

  public UntilUnlifeObservableTransformer(@Nonnull Observable<R> lifecycle, @Nonnull R event) {
    this.lifecycle = lifecycle;
    this.event = event;
  }

  @Override
  public Observable<T> call(Observable<T> source) {
    return source.lift(new OperatorUntilUnlife(takeUnlifeEvent(lifecycle, event)));
  }

  @Nonnull
  @Override
  public Single.Transformer<T, T> forSingle() {
    return new UntilEventSingleTransformer<>(lifecycle, event);
  }

  @Nonnull
  @Override
  public Completable.Transformer forCompletable() {
    return new UntilEventCompletableTransformer<>(lifecycle, event);
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

    if (!lifecycle.equals(that.lifecycle)) {
      return false;
    }
    return event.equals(that.event);
  }

  @Override
  public int hashCode() {
    int result = lifecycle.hashCode();
    result = 31 * result + event.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "UntilUnlifeObservableTransformer{" +
        "lifecycle=" + lifecycle +
        ", event=" + event +
        '}';
  }
}
