package com.github.satoshun.reactive.unlife;

import com.github.satoshun.reactive.unlife.internal.ObservableRxUnLifeUntil;
import com.github.satoshun.reactive.unlife.internal.SingleRxUnLifeUntil;

import javax.annotation.Nonnull;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Continues a subscription until it sees a particular lifecycle event.
 */
public class UnLifeTransformer<T> implements
    ObservableTransformer<T, T>,
    SingleTransformer<T, T> {

  private final Observable<?> lifecycle;

  public UnLifeTransformer(@Nonnull Observable<?> lifecycle) {
    this.lifecycle = lifecycle;
  }

  @Override public ObservableSource<T> apply(Observable<T> upstream) {
    return RxJavaPlugins.onAssembly(new ObservableRxUnLifeUntil<>(upstream, lifecycle));
  }

  @Override public SingleSource<T> apply(Single<T> upstream) {
    return RxJavaPlugins.onAssembly(new SingleRxUnLifeUntil<>(upstream, lifecycle.toFlowable(BackpressureStrategy.LATEST)));
  }
}
