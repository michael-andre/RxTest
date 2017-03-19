package com.wapplix.rxtest;

import android.accounts.AccountManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wapplix.rx.RxCallback;
import com.wapplix.rx.RxFragment;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements RxCallback<String> {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxFragment.host(longWork())
                        .progressDialog("Test")
                        .target(MainActivityFragment.this, "task");
            }
        });
    }

    public static Single<String> longWork() {
        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Log.i("Work", "Start");
                Thread.sleep(5000);
                Log.i("Work", "End");
                return "Done";
            }
        }).subscribeOn(Schedulers.computation());
    }


    @Override
    public Disposable onSubscribe(Observable<? extends String> observable, String tag) {
        Log.i("Target", "Subscribe");
        return observable.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i("Target", "Accept");
                Snackbar.make(getView(), s, Snackbar.LENGTH_LONG).show();
            }
        });
    }

}