/**
 * Copyright (c) 2016-present, RxJava Contributors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package com.github.satoshun.reactive.unlife.internal;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.plugins.RxJavaPlugins;

public class SingleRxUnLifeUntil<T, U> extends Single<T> {

  private final SingleSource<T> source;
  private final Publisher<U> other;

  public SingleRxUnLifeUntil(SingleSource<T> source, Publisher<U> other) {
    this.source = source;
    this.other = other;
  }

  @Override
  protected void subscribeActual(SingleObserver<? super T> observer) {
    UnlifeUntilMainObserver<T> parent = new UnlifeUntilMainObserver<>(observer);
    observer.onSubscribe(parent);

    other.subscribe(parent.other);
    source.subscribe(parent);
  }

  static final class UnlifeUntilMainObserver<T> extends AtomicReference<Disposable>
      implements SingleObserver<T>, Disposable {

    final SingleObserver<? super T> actual;
    final UnlifeUntilOtherSubscriber other;

    UnlifeUntilMainObserver(SingleObserver<? super T> actual) {
      this.actual = actual;
      this.other = new UnlifeUntilOtherSubscriber(this);
    }

    @Override
    public void dispose() {
      DisposableHelper.dispose(this);
    }

    @Override
    public boolean isDisposed() {
      return DisposableHelper.isDisposed(get());
    }

    @Override
    public void onSubscribe(Disposable d) {
      DisposableHelper.setOnce(this, d);
    }

    @Override
    public void onSuccess(T value) {
      other.dispose();

      Disposable a = get();
      if (a != DisposableHelper.DISPOSED) {
        a = getAndSet(DisposableHelper.DISPOSED);
        if (a != DisposableHelper.DISPOSED) {
          actual.onSuccess(value);
        }
      }
    }

    @Override
    public void onError(Throwable e) {
      other.dispose();

      Disposable a = get();
      if (a != DisposableHelper.DISPOSED) {
        a = getAndSet(DisposableHelper.DISPOSED);
        if (a != DisposableHelper.DISPOSED) {
          actual.onError(e);
          return;
        }
      }
      RxJavaPlugins.onError(e);
    }

    void otherError() {
      Disposable a = get();
      if (a != DisposableHelper.DISPOSED) {
        a = getAndSet(DisposableHelper.DISPOSED);
        if (a != DisposableHelper.DISPOSED) {
          if (a != null) {
            a.dispose();
          }
        }
      }
    }
  }

  static final class UnlifeUntilOtherSubscriber extends AtomicReference<Subscription>
      implements Subscriber<Object> {

    final UnlifeUntilMainObserver<?> parent;

    UnlifeUntilOtherSubscriber(UnlifeUntilMainObserver<?> parent) {
      this.parent = parent;
    }

    @Override
    public void onSubscribe(Subscription s) {
      if (SubscriptionHelper.setOnce(this, s)) {
        s.request(Long.MAX_VALUE);
      }
    }

    @Override
    public void onNext(Object t) {
      cancel();
    }

    @Override
    public void onError(Throwable t) {
      cancel();
    }

    @Override
    public void onComplete() {
      cancel();
    }

    void cancel() {
      if (SubscriptionHelper.cancel(this)) {
        dispose();
        parent.otherError();
      }
    }

    void dispose() {
      SubscriptionHelper.cancel(this);
    }
  }
}
