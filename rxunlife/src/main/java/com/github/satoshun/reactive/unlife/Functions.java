package com.github.satoshun.reactive.unlife;

import rx.exceptions.Exceptions;
import rx.functions.Func1;

final class Functions {

  static final Func1<Throwable, Boolean> RESUME_FUNCTION = new Func1<Throwable, Boolean>() {
    @Override
    public Boolean call(Throwable throwable) {
      if (throwable instanceof OutsideLifecycleException) {
        return true;
      }

      //noinspection ThrowableResultOfMethodCallIgnored
      Exceptions.propagate(throwable);
      return false;
    }
  };

  static final Func1<Boolean, Boolean> SHOULD_COMPLETE = new Func1<Boolean, Boolean>() {
    @Override
    public Boolean call(Boolean shouldComplete) {
      return shouldComplete;
    }
  };

  private Functions() {
    throw new AssertionError("No instances!");
  }
}
