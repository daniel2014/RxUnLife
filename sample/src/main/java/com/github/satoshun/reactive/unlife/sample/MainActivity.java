package com.github.satoshun.reactive.unlife.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.satoshun.reactive.unlife.RxUnLife;
import com.github.satoshun.reactive.unlife.android.ActivityEvent;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

  private final BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();
  private final GithubClient github = new Retrofit.Builder()
      .client(new OkHttpClient.Builder().build())
      .baseUrl("https://api.github.com/")
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
      .create(GithubClient.class);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    lifecycle.onNext(ActivityEvent.CREATE);

    setContentView(R.layout.main_act);

    sampleObservable();
    sampleSingle();
  }

  private void sampleObservable() {
    // unsubscribe when CREATE cycle: this is not called
    github.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnDispose(() -> Log.d("observable:unlife", "doOnDispose"))
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.CREATE))
        .subscribe(
            v -> Log.d("observable:unlife", String.valueOf(v)),
            e -> Log.d("observable:unlife", String.valueOf(e)),
            () -> Log.d("observable:unlife", "completed"));

    // unsubscribe when PAUSE cycle: this is called
    github.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnDispose(() -> Log.d("observable success", "doOnDispose"))
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.PAUSE))
        .subscribe(
            v -> Log.d("observable success", String.valueOf(v)),
            e -> Log.d("observable success", String.valueOf(e)),
            () -> Log.d("observable success", "completed"));

    // unsubscribe when PAUSE cycle: this is called
    github.observableError()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnDispose(() -> Log.d("observable error", "doOnDispose"))
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
        .doOnDispose(() -> Log.d("single:unlife", "doOnDispose"))
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.CREATE))
        .subscribe(
            v -> Log.d("single:unlife", String.valueOf(v)),
            e -> Log.d("single:unlife", String.valueOf(e)));

    github.single()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnDispose(() -> Log.d("single success", "doOnDispose"))
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.STOP))
        .subscribe(
            v -> Log.d("single success", String.valueOf(v)),
            e -> Log.d("single success", String.valueOf(e)));

    github.singleError()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnDispose(() -> Log.d("single error", "doOnDispose"))
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.STOP))
        .subscribe(
            v -> Log.d("single error", String.valueOf(v)),
            e -> Log.d("single error", String.valueOf(e)));
  }

  interface GithubClient {
    @GET("meta") Observable<Object> observable();

    @GET("meta/hoge") Observable<Object> observableError();

    @GET("meta") Single<Object> single();

    @GET("meta/hoge") Single<Object> singleError();

    @GET("meta") Completable completable();

    @GET("meta/hoge") Completable completableError();
  }
}
