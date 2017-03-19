package com.wapplix.rx;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by Mike on 18/03/2017.
 */

public class RxFragment<T> extends Fragment {

    private Observable<T> mObservable;
    private Disposable mTargetDisposable;
    private boolean mRemoveFlag;

    public static <T> RxFragment<T> host(Observable<T> observable) {
        RxFragment<T> fragment = new RxFragment<>();
        fragment.setObservable(observable);
        return fragment;
    }

    public static <T> RxFragment<T> host(Single<T> single) {
        return host(single.toObservable());
    }

    protected void setObservable(@NonNull Observable<T> observable) {
        mObservable = observable
                .observeOn(AndroidSchedulers.mainThread())
                .replay(1)
                .autoConnect()
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        safeRemove();
                    }
                });
    }

    public <TCallbackFragment extends Fragment & RxCallback<T>> RxFragment<T> target(TCallbackFragment target, String tag) {
        setTargetFragment(target, 0);
        return target(target.getFragmentManager(), tag);
    }

    public <TCallbackActivity extends FragmentActivity & RxCallback<T>> RxFragment<T> target(TCallbackActivity target, String tag) {
        return target(target.getSupportFragmentManager(), tag);
    }

    private RxFragment<T> target(FragmentManager fm, String tag) {
        fm.beginTransaction()
                .add(this, tag)
                .commit();
        return this;
    }

    public RxFragment<T> progressDialog(final String message) {
        mObservable = mObservable.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                String tag = getTag() + "_progress";
                if (getFragmentManager().findFragmentByTag(tag) == null) {
                    RxProgressFragment progressFragment = RxProgressFragment.newInstance(message);
                    progressFragment.show(getFragmentManager(), getTag() + "_progress");
                }
            }
        }).doOnTerminate(new Action() {
            @Override
            public void run() throws Exception {
                Fragment progressFragment = getFragmentManager().findFragmentByTag(getTag() + "_progress");
                if (progressFragment instanceof RxProgressFragment) {
                    ((RxProgressFragment) progressFragment).dismissAllowingStateLoss();
                }
            }
        });
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mObservable != null) {
            mTargetDisposable = getTarget().onSubscribe(mObservable, getTag());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRemoveFlag) safeRemove();
    }

    @Override
    public void onStop() {
        if (mTargetDisposable != null) {
            mTargetDisposable.dispose();
            mTargetDisposable = null;
        }
        super.onStop();
    }

    private void safeRemove() {
        if (isResumed()) {
            getFragmentManager().beginTransaction()
                    .remove(RxFragment.this)
                    .commit();
        } else {
            mRemoveFlag = true;
        }
    }

    @NonNull
    private RxCallback<? super T> getTarget() {
        Fragment fragment = getTargetFragment();
        if (fragment instanceof RxCallback) {
            return (RxCallback<? super T>) fragment;
        }
        Activity activity = getActivity();
        if (activity instanceof RxCallback) {
            return (RxCallback<? super T>) activity;
        }
        throw new IllegalStateException("Couldn't find target");
    }

}
