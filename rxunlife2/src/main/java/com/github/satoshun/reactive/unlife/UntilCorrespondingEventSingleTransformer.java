package com.github.satoshun.reactive.unlife;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Function;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 * <p>
 * That lifecycle event is determined based on what stage we're at in
 * the current lifecycle.
 */
final class UntilCorrespondingEventSingleTransformer<T, R> implements SingleTransformer<T, T> {

  final Observable<R> sharedLifecycle;
  final Function<R, R> correspondingEvents;

  public UntilCorrespondingEventSingleTransformer(@Nonnull Observable<R> sharedLifecycle,
                                                  @Nonnull Function<R, R> correspondingEvents) {
    this.sharedLifecycle = sharedLifecycle;
    this.correspondingEvents = correspondingEvents;
  }

  @Override public SingleSource<T> apply(Single<T> upstream) {
    return null;
  }
}
