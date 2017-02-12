/**
 * Copyright (c) 2017 SatoShun
 *
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

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.ArrayCompositeDisposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.observers.SerializedObserver;

public class ObservableRxUnLifeUntil<T, U> extends Observable<T> {

  private final ObservableSource<T> source;
  private final ObservableSource<? extends U> other;

  public ObservableRxUnLifeUntil(ObservableSource<T> source, ObservableSource<? extends U> other) {
    this.source = source;
    this.other = other;
  }

  @Override
  public void subscribeActual(Observer<? super T> child) {
    final SerializedObserver<T> serial = new SerializedObserver<>(child);
    final ArrayCompositeDisposable frc = new ArrayCompositeDisposable(2);
    final RxUnLifeUntil<T> tus = new RxUnLifeUntil<>(serial, frc);

    child.onSubscribe(frc);

    other.subscribe(new Observer<U>() {
      @Override
      public void onSubscribe(Disposable s) {
        frc.setResource(1, s);
      }

      @Override
      public void onNext(U t) {
        frc.dispose();
      }

      @Override
      public void onError(Throwable t) {
        frc.dispose();
      }

      @Override
      public void onComplete() {
        frc.dispose();
      }
    });

    source.subscribe(tus);
  }

  static final class RxUnLifeUntil<T> extends AtomicBoolean implements Observer<T> {

    final Observer<? super T> actual;
    final ArrayCompositeDisposable frc;

    Disposable s;

    RxUnLifeUntil(Observer<? super T> actual, ArrayCompositeDisposable frc) {
      this.actual = actual;
      this.frc = frc;
    }

    @Override public void onSubscribe(Disposable s) {
      if (DisposableHelper.validate(this.s, s)) {
        this.s = s;
        frc.setResource(0, s);
      }
    }

    @Override
    public void onNext(T t) {
      actual.onNext(t);
    }

    @Override
    public void onError(Throwable t) {
      frc.dispose();
      actual.onError(t);
    }

    @Override
    public void onComplete() {
      frc.dispose();
      actual.onComplete();
    }
  }
}
