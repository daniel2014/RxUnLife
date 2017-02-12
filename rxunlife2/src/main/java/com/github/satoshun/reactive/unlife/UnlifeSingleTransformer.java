package com.github.satoshun.reactive.unlife;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 */
final class UnlifeSingleTransformer<T, R> implements SingleTransformer<T, T> {

  final Observable<R> lifecycle;
  final R event;

  UnlifeSingleTransformer(@Nonnull Observable<R> lifecycle, @Nonnull R event) {
    this.lifecycle = lifecycle;
    this.event = event;
  }

  @Override public SingleSource<T> apply(Single<T> upstream) {
    return null;
  }
}
