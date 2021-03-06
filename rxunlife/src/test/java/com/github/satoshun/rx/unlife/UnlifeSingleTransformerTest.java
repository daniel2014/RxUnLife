package com.github.satoshun.rx.unlife;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import rx.Single;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

public class UnlifeSingleTransformerTest {

  PublishSubject<String> lifecycle;
  TestSubscriber<String> testSubscriber;
  TestScheduler testScheduler; // Since Single is not backpressure aware, use this to simulate it taking time

  @Before
  public void setup() {
    lifecycle = PublishSubject.create();
    testSubscriber = new TestSubscriber<>();
    testScheduler = new TestScheduler();
  }

  @Test
  public void noEvents() {
    Single.just("1")
        .delay(1, TimeUnit.MILLISECONDS, testScheduler)
        .compose(new UnlifeSingleTransformer<String, String>(lifecycle, "stop"))
        .subscribe(testSubscriber);

    testSubscriber.assertNoValues();

    testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
    testSubscriber.assertValue("1");
    testSubscriber.assertCompleted();
  }

  @Test
  public void oneWrongEvent() {
    Single.just("1")
        .delay(1, TimeUnit.MILLISECONDS, testScheduler)
        .compose(new UnlifeSingleTransformer<String, String>(lifecycle, "stop"))
        .subscribe(testSubscriber);

    testSubscriber.assertNoValues();

    lifecycle.onNext("keep going");
    testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

    testSubscriber.assertValue("1");
    testSubscriber.assertCompleted();
  }

  @Test
  public void twoEvents() {
    Single.just("1")
        .delay(1, TimeUnit.MILLISECONDS, testScheduler)
        .compose(new UnlifeSingleTransformer<String, String>(lifecycle, "stop"))
        .subscribe(testSubscriber);

    lifecycle.onNext("keep going");
    testSubscriber.assertNoErrors();

    lifecycle.onNext("stop");
    testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

    testSubscriber.assertNoValues();
    testSubscriber.assertNoErrors();
  }
}