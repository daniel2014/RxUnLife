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

import org.junit.Before;
import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class RxUnLifeTest {

  private Observable<Object> observable;

  @Before public void setup() {
    // Simulate an actual lifecycle (hot Observable that does not end)
    observable = PublishSubject.create().hide();
  }

  @Test(expected = NullPointerException.class)
  public void testBindThrowsOnNullLifecycle() {
    //noinspection ResourceType
    RxUnLife.bind((Observable) null);
  }

  @Test(expected = NullPointerException.class)
  public void testBindUntilThrowsOnNullLifecycle() {
    //noinspection ResourceType
    RxUnLife.bindUntilEvent(null, new Object());
  }

  @Test(expected = NullPointerException.class)
  public void testBindUntilThrowsOnNullEvent() {
    BehaviorSubject<Object> lifecycle = BehaviorSubject.create();
    //noinspection ResourceType
    RxUnLife.bindUntilEvent(lifecycle, null);
  }
}
