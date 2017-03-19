package com.wapplix.rx;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Mike on 18/03/2017.
 */

public interface RxCallback<T> {

    Disposable onSubscribe(Observable<? extends T> observable, String tag);

}
