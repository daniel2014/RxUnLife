package com.github.satoshun.rx.unlife.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.satoshun.rx.unlife.RxUnLife;
import com.github.satoshun.rx.unlife.android.ActivityEvent;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class MainActivity extends AppCompatActivity {

  private final BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

  private GithubClient github;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    lifecycle.onNext(ActivityEvent.CREATE);

    setContentView(R.layout.main_act);

    github = new Retrofit.Builder()
        .client(new OkHttpClient.Builder().build())
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build()
        .create(GithubClient.class);

    sampleObservable();
    sampleSingle();
  }

  private void sampleObservable() {
    // unsubscribe when CREATE cycle: this is not called
    github.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnUnsubscribe(() -> Log.d("observable:unlife", "doOnUnsubscribe"))
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.CREATE))
        .subscribe(
            v -> Log.d("observable:unlife", String.valueOf(v)),
            e -> Log.d("observable:unlife", String.valueOf(e)),
            () -> Log.d("observable:unlife", "completed"));

    // unsubscribe when PAUSE cycle: this is called
    github.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnUnsubscribe(() -> Log.d("observable success", "doOnUnsubscribe"))
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.PAUSE))
        .subscribe(
            v -> Log.d("observable success", String.valueOf(v)),
            e -> Log.d("observable success", String.valueOf(e)),
            () -> Log.d("observable success", "completed"));

    // unsubscribe when PAUSE cycle: this is called
    github.observableError()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnUnsubscribe(() -> Log.d("observable error", "doOnUnsubscribe"))
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.PAUSE))
        .subscribe(
            v -> Log.d("observable error", String.valueOf(v)),
            e -> Log.d("observable error", String.valueOf(e)),
            () -> Log.d("observable error", "completed"));
  }

  private void sampleSingle() {
    github.single()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnUnsubscribe(() -> Log.d("single:unlife", "doOnUnsubscribe"))
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.CREATE).forSingle())
        .subscribe(
            v -> Log.d("single:unlife", String.valueOf(v)),
            e -> Log.d("single:unlife", String.valueOf(e)));

    github.single()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnUnsubscribe(() -> Log.d("single success", "doOnUnsubscribe"))
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.STOP).forSingle())
        .subscribe(
            v -> Log.d("single success", String.valueOf(v)),
            e -> Log.d("single success", String.valueOf(e)));

    github.singleError()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnUnsubscribe(() -> Log.d("single error", "doOnUnsubscribe"))
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.STOP).forSingle())
        .subscribe(
            v -> Log.d("single error", String.valueOf(v)),
            e -> Log.d("single error", String.valueOf(e)));
  }

  interface GithubClient {
    @GET("meta") Observable<Object> observable();

    @GET("meta/hoge") Observable<Object> observableError();

    @GET("meta") Single<Object> single();

    @GET("meta/hoge") Single<Object> singleError();
  }
}
