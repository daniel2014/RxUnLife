package com.github.satoshun.reactive.unlife;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import rx.Observable;
import rx.Single;

/**
 * A Transformer that works for two RxJava types ({@link Observable}, {@link Single}).
 * <p>
 * Out of the box, it works for Observable. But it can be easily converted for {@link Single}.
 */
public interface UnLifeTransformer<T> extends Observable.Transformer<T, T> {

  /**
   * @return a version of this Transformer for {@link Single} streams.
   */
  @Nonnull
  @CheckReturnValue
  // Implementation note: We use a different generic to cover some insane case in Java 8 inference.
  // See more here: https://github.com/trello/RxLifecycle/issues/126
  <U> Single.Transformer<U, U> forSingle();
}
