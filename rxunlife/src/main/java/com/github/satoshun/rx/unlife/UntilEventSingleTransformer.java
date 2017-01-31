package com.github.satoshun.rx.unlife;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.Single;

import static com.github.satoshun.rx.unlife.TakeUntilGenerator.takeUnlifeEvent;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 */
final class UntilEventSingleTransformer<T, R> implements Single.Transformer<T, T> {

  final Observable<R> lifecycle;
  final R event;

  public UntilEventSingleTransformer(@Nonnull Observable<R> lifecycle, @Nonnull R event) {
    this.lifecycle = lifecycle;
    this.event = event;
  }

  @Override
  public Single<T> call(Single<T> source) {
    return source.takeUntil(takeUnlifeEvent(lifecycle, event));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    UntilEventSingleTransformer<?, ?> that = (UntilEventSingleTransformer<?, ?>) o;

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
    return "UntilEventSingleTransformer{" +
        "lifecycle=" + lifecycle +
        ", event=" + event +
        '}';
  }
}
