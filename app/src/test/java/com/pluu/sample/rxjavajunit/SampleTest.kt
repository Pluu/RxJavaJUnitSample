package com.pluu.sample.rxjavajunit

import com.pluu.sample.rxjavajunit.utils.RxJavaUncaughtErrorRule
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations

class SampleTest {

    @get:Rule
    val uncaughtRxJavaErrors = RxJavaUncaughtErrorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        RxJavaPlugins.setComputationSchedulerHandler {
            Schedulers.trampoline()
        }
        RxAndroidPlugins.setMainThreadSchedulerHandler {
            Schedulers.trampoline()
        }
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
    }

    @Test
    fun test1() {
        Sample().use { sample ->
            sample.test { value ->
                assertTrue(value > 100)
            }
        }

        uncaughtRxJavaErrors.assertNoErrors()
    }
}