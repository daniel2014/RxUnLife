package com.github.satoshun.rx.unlife;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.Subscription;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class RxUnLifeTest {
  private Observable<Object> observable;

  @Before
  public void setup() {
    // Simulate an actual lifecycle (hot Observable that does not end)
    observable = PublishSubject.create().asObservable();
  }

  @Test
  public void testBindLifecycle() {
    BehaviorSubject<Object> lifecycle = BehaviorSubject.create();
    Subscription attachSub = observable.compose(RxUnLife.bind(lifecycle)).subscribe();
    assertFalse(attachSub.isUnsubscribed());
    lifecycle.onNext(new Object());
    assertTrue(attachSub.isUnsubscribed());
  }

  @Test
  public void testBindLifecycleOtherObject() {
    // Ensures it works with other types as well, and not just "Object"
    BehaviorSubject<String> lifecycle = BehaviorSubject.create();
    Subscription attachSub = observable.compose(RxUnLife.bind(lifecycle)).subscribe();
    assertFalse(attachSub.isUnsubscribed());
    lifecycle.onNext("");
    assertTrue(attachSub.isUnsubscribed());
  }

  // Null checks

  @Test(expected = NullPointerException.class)
  public void testBindThrowsOnNullLifecycle() {
    //noinspection ResourceType
    RxUnLife.bind((Observable) null);
  }
}
