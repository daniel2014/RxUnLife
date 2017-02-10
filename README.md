# RxUnLife

The utilities provided here allow for automatic unsubscription of sequences based on `Activity` or `Fragment`
lifecycle events. This capability is useful in Android, where incomplete subscriptions can cause memory leaks.


## What different between RxUnLife and RxLifeCycle

- RxLifeCycle provided automatic **completion**
  - Observable: call `onComplete` when stream is ended
  - Single: call `onError` when stream is ended
- RxUnLife provided automatic **unsubscription**
  - Observable: doesn't call any Subscriber methods when stream is ended
  - Single: doesn't call any Subscriber methods when stream is ended

I want a unsubscription stream. but RxLifeCycle(takeUntil operator) creates completion stream.


## Install

```gradle
repositories {
  maven { url 'https://jitpack.io' }
}

compile 'com.github.satoshun.RxUnLife:rxunlife:0.2.0'
compile 'com.github.satoshun.RxUnLife:rxunlife-android:0.2.0' 
```


## Usage

You must provide an `Observable<ActivityEvent>` or `Observable<FragmentEvent>` that gives
RxUnLife the information needed to complete the sequence at the correct time.

You can then end the sequence explicitly when an event occurs:

```java
myObservable
    .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.DESTROY))
    .subscribe();
```

## Single

RxUnLife supports `Single` via the `UnLifeTransformer`. You can convert returned `UnLifeTransformer`
into a `Single.Transformer` via the `forSingle()` method:

```java
mySingle
    .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.DESTROY).forSingle())
    .subscribe();
```

## License

    Copyright (C) 2017 Sato Shun

    Copyright (C) 2016 Trello

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    