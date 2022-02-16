package com.pluu.sample.rxjavajunit.utils

import io.reactivex.rxjava3.plugins.RxJavaPlugins
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.concurrent.LinkedBlockingDeque

class RxJavaUncaughtErrorRule : TestWatcher() {

    private val errors = LinkedBlockingDeque<Throwable>()

    override fun starting(description: Description) {
        RxJavaPlugins.setErrorHandler { t -> errors.add(t) }
    }

    override fun finished(description: Description) {
        RxJavaPlugins.setErrorHandler(null)
    }

    fun getErrors(): List<Throwable> = errors.toList()

    fun hasErrors(): Boolean = errors.peek() != null

    fun assertNoErrors() {
        if (hasErrors()) {
            throw AssertionError(
                "Expected no errors but RxJavaPlugins received " + getErrors()
            )
        }
    }
}