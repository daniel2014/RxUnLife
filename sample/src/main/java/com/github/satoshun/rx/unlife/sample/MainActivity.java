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
  }

  private void sampleObservable() {
    // release when CREATE cycle: this is not called
    github.observable()
        .doAfterTerminate(() -> Log.d("create: not call", "doAfterTerminate"))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.CREATE))
        .subscribe(
            v -> Log.d("create: not call", String.valueOf(v)),
            e -> Log.d("create: not call", String.valueOf(e)),
            () -> Log.d("create: not call", "completed"));

    // release when PAUSE cycle: this is called
    github.observable()
        .doAfterTerminate(() -> Log.d("observable: call", "doAfterTerminate"))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(RxUnLife.bindUntilEvent(lifecycle, ActivityEvent.PAUSE))
        .subscribe(
            v -> Log.d("observable: call", String.valueOf(v)),
            e -> Log.d("observable: call", String.valueOf(e)),
            () -> Log.d("observable: call", "completed"));
  }

  interface GithubClient {
    @GET("meta") Observable<Object> observable();

    @GET("meta") Single<Object> single();
  }
}
