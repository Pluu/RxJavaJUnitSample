package com.pluu.sample.rxjavajunit

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class Sample : AutoCloseable {
    private val disposable = CompositeDisposable()

    fun test(callback: (Int) -> Unit) {
        disposable += Observable.just(1)
            .delay(2, TimeUnit.SECONDS)
            .map {
                it * it
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                printNow("Block Start")
            }
            .doOnTerminate {
                printNow("Block End")
            }
            .subscribe {
                callback(it)
            }
    }

    override fun close() {
        if (disposable.isDisposed) {
            disposable.clear()
        }
    }
}
