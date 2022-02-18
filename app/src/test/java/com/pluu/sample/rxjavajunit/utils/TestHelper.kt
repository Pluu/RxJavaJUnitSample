package com.pluu.sample.rxjavajunit.utils

import io.reactivex.rxjava3.exceptions.OnErrorNotImplementedException
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import java.util.*

///////////////////////////////////////////////////////////////////////////
// Origin : https://github.com/ReactiveX/RxJava/blob/3.x/src/test/java/io/reactivex/rxjava3/testsupport/TestHelper.java
///////////////////////////////////////////////////////////////////////////

object TestHelper {
    fun trackPluginErrors(): List<Throwable> {
        val list: MutableList<Throwable> = Collections.synchronizedList(ArrayList())
        RxJavaPlugins.setErrorHandler {
            list.add(it)
        }
        return list
    }

    fun assertUndeliverable(list: List<Throwable>, index: Int, clazz: Class<out Throwable>) {
        var ex = list[index]
        if (ex !is OnErrorNotImplementedException && // OnError 미정의한 경우의 에러
            ex !is UndeliverableException // RxJavaPlugins.onError를 통해서 유입된 에러 유무
        ) {
            val err = AssertionError(
                "Outer exception UndeliverableException expected but got " + list[index]
            )
            err.initCause(list[index])
            throw err
        }
        ex = ex.cause!!
        if (!clazz.isInstance(ex)) {
            val err = AssertionError(
                "Inner exception " + clazz + " expected but got " + list[index]
            )
            err.initCause(list[index])
            throw err
        }
    }
}