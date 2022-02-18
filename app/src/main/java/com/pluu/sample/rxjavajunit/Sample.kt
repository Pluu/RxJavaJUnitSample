package com.pluu.sample.rxjavajunit

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

class Sample : AutoCloseable {
    private val compositeDisposable = CompositeDisposable()

    fun testParam1(callback: (data: Int) -> Unit) {
        testParam1(false, callback)
    }

    fun testParam1EarlyFail(callback: (data: Int) -> Unit) {
        testParam1(true, callback)
    }

    private fun testParam1(
        hasEarlyFail: Boolean,
        callback: (data: Int) -> Unit
    ) {
        Single.just(1)
            .subscribe { value ->
                if (hasEarlyFail) {
                    value / 0
                }
                callback(value)
            }.addComposable(compositeDisposable)
    }

    fun testParam2(callback: (data: List<Int>, key: Int?) -> Unit) {
        testParam2(false, callback)
    }

    fun testParam2EarlyFail(callback: (data: List<Int>, key: Int?) -> Unit) {
        testParam2(true, callback)
    }

    private fun testParam2(
        hasEarlyFail: Boolean,
        callback: (data: List<Int>, key: Int?) -> Unit
    ) {
        Single.just(1)
            .subscribe { value ->
                if (hasEarlyFail) {
                    value / 0
                }
                callback((1..10).toList(), value)
            }.addComposable(compositeDisposable)
    }

    fun testListenerParam1(callback: SampleListener) {
        testListenerParam1(false, callback)
    }

    fun testListenerParam1EarlyFail(callback: SampleListener) {
        testListenerParam1(true, callback)
    }

    private fun testListenerParam1(
        hasEarlyFail: Boolean,
        callback: SampleListener
    ) {
        Single.just(1)
            .subscribe { value ->
                if (hasEarlyFail) {
                    value / 0
                }
                callback.callback1(1)
            }.addComposable(compositeDisposable)
    }

    fun testListenerParam2(callback: SampleListener) {
        testListenerParam2(false, callback)
    }

    fun testListenerParam2EarlyFail(callback: SampleListener) {
        testListenerParam2(true, callback)
    }

    private fun testListenerParam2(
        hasEarlyFail: Boolean,
        callback: SampleListener
    ) {
        Single.just(1)
            .subscribe { value ->
                if (hasEarlyFail) {
                    value / 0
                }
                callback.callback2((1..10).toList(), value)
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