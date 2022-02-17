package com.pluu.sample.rxjavajunit

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class Sample : AutoCloseable {
    private val compositeDisposable = CompositeDisposable()

    fun testParam1(callback: (data: Int) -> Unit) {
        Observable.just(1)
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
            }.addComposable(compositeDisposable)
    }

    fun testParam2(callback: (data: List<Int>, key: Int?) -> Unit) {
        Observable.just(1)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                printNow("Block Start")
            }
            .doOnTerminate {
                printNow("Block End")
            }
            .subscribe {
                callback((1..10).toList(), it)
            }.addComposable(compositeDisposable)
    }

    fun testListenerParam1(callback: SampleListener) {
        Observable.just(1)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                printNow("Block Start")
            }
            .doOnTerminate {
                printNow("Block End")
            }
            .subscribe {
                callback.callback1(1)
            }.addComposable(compositeDisposable)
    }

    fun testListenerParam2(callback: SampleListener) {
        Observable.just(1)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                printNow("Block Start")
            }
            .doOnTerminate {
                printNow("Block End")
            }
            .subscribe {
                callback.callback2((1..10).toList(), it)
            }.addComposable(compositeDisposable)
    }

    override fun close() {
        if (compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    interface SampleListener {
        fun callback1(data: Int)

        fun callback2(data: List<Int>, key: Int?)
    }
}

fun Disposable.addComposable(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}