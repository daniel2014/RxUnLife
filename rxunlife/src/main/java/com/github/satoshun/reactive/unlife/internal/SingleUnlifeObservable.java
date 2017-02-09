/**
 * Copyright 2017 Sato Shun.
 * Copyright 2016 Netflix, Inc.
 * <p>
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

package com.github.satoshun.reactive.unlife.internal;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.plugins.RxJavaHooks;

public class SingleUnlifeObservable<T, U> implements Single.OnSubscribe<T> {

  final Single<T> source;
  final Observable<? extends U> other;

  public SingleUnlifeObservable(Single<T> source, Observable<? extends U> other) {
    this.source = source;
    this.other = other;
  }

  @Override
  public void call(SingleSubscriber<? super T> t) {
    UnlifeSourceSubscriber<T, U> parent = new UnlifeSourceSubscriber<>(t);

    t.add(parent);
    other.subscribe(parent.other);
    source.subscribe(parent);
  }

  static final class UnlifeSourceSubscriber<T, U> extends SingleSubscriber<T> {

    final SingleSubscriber<? super T> actual;
    final AtomicBoolean once;
    final Subscriber<U> other;

    UnlifeSourceSubscriber(SingleSubscriber<? super T> actual) {
      this.actual = actual;
      this.once = new AtomicBoolean();
      this.other = new OtherSubscriber();
      add(other);
    }

    @Override
    public void onSuccess(T value) {
      if (once.compareAndSet(false, true)) {
        unsubscribe();

        actual.onSuccess(value);
      }
    }

    @Override
    public void onError(Throwable error) {
      if (once.compareAndSet(false, true)) {
        unsubscribe();
        actual.onError(error);
      } else {
        RxJavaHooks.onError(error);
      }
    }

    final class OtherSubscriber extends Subscriber<U> {
      @Override
      public void onNext(U value) {
        internalUnSubscribe();
      }

      @Override
      public void onError(Throwable error) {
        internalUnSubscribe();
      }

      @Override
      public void onCompleted() {
        internalUnSubscribe();
      }

      private void internalUnSubscribe() {
        if (once.compareAndSet(false, true)) {
          UnlifeSourceSubscriber.this.unsubscribe();
          actual.unsubscribe();
        }
      }
    }
  }
}
