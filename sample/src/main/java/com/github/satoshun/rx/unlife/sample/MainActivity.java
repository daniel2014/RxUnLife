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
    // release when CREATE cycle: this is not called
    github.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.CREATE))
        .subscribe(
            v -> Log.d("observable: not call", String.valueOf(v)),
            e -> Log.d("observable: not call", String.valueOf(e)),
            () -> Log.d("observable: not call", "completed"));

    // release when PAUSE cycle: this is called
    github.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.PAUSE))
        .subscribe(
            v -> Log.d("observable success: call", String.valueOf(v)),
            e -> Log.d("observable success: call", String.valueOf(e)),
            () -> Log.d("observable success: call", "completed"));

    // release when PAUSE cycle: this is called
    github.observableError()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.PAUSE))
        .subscribe(
            v -> Log.d("observable error: call", String.valueOf(v)),
            e -> Log.d("observable error: call", String.valueOf(e)),
            () -> Log.d("observable error: call", "completed"));
  }

  private void sampleSingle() {
    github.single()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.CREATE).forSingle())
        .subscribe(
            v -> Log.d("single: not call", String.valueOf(v)),
            e -> Log.d("single: not call", String.valueOf(e)));

    github.single()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.STOP).forSingle())
        .subscribe(
            v -> Log.d("single success: call", String.valueOf(v)),
            e -> Log.d("single success: call", String.valueOf(e)));

    github.singleError()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.STOP).forSingle())
        .subscribe(
            v -> Log.d("single error: call", String.valueOf(v)),
            e -> Log.d("single error: call", String.valueOf(e)));
  }

  interface GithubClient {
    @GET("meta") Observable<Object> observable();

    @GET("meta/hoge") Observable<Object> observableError();

    @GET("meta") Single<Object> single();

    @GET("meta/hoge") Single<Object> singleError();
  }
}
