/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.satoshun.reactive.unlife;

import com.github.satoshun.reactive.unlife.internal.Preconditions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Predicate;

public class RxUnLife {

  private RxUnLife() {
    throw new AssertionError("No instances");
  }

  /**
   * Binds the given source to a lifecycle.
   * <p>
   * When the lifecycle event occurs, the source will cease to emit any notifications.
   * <p>
   * {@code source.compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.STOP)).subscribe()}
   */
  @Nonnull
  @CheckReturnValue
  public static <T, R> UnLifeTransformer<T> bindUntilEvent(
      @Nonnull final Observable<R> lifecycle,
      @Nonnull final R event) {
    Preconditions.checkNotNull(lifecycle, "lifecycle == null");
    Preconditions.checkNotNull(event, "event == null");
    return bind(takeUntilEvent(lifecycle, event));
  }

  /**
   * Binds the given source to a lifecycle.
   * <p>
   * Use with {@link Observable#compose(ObservableTransformer)}:
   * {@code source.compose(RxUnLife.bind(lifecycle)).subscribe()}
   * <p>
   * This helper automatically determines (based on the lifecycle sequence itself) when the source
   * should stop emitting items. Note that for this method, it assumes <em>any</em> event
   * emitted by the given lifecycle indicates that the lifecycle is over.
   *
   * @param lifecycle the lifecycle sequence
   * @return a reusable {@link ObservableTransformer} that unsubscribes the source whenever the lifecycle emits
   */
  @Nonnull
  @CheckReturnValue
  public static <T, R> UnLifeTransformer<T> bind(@Nonnull final Observable<R> lifecycle) {
    Preconditions.checkNotNull(lifecycle, "lifecycle == null");
    return new UnLifeTransformer<>(lifecycle);
  }

  private static <R> Observable<R> takeUntilEvent(final Observable<R> lifecycle, final R event) {
    return lifecycle.filter(new Predicate<R>() {
      @Override
      public boolean test(R lifecycleEvent) throws Exception {
        return lifecycleEvent.equals(event);
      }
    });
  }
}
