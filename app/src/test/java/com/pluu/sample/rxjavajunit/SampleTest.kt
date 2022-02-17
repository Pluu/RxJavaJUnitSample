package com.pluu.sample.rxjavajunit

import com.pluu.sample.rxjavajunit.utils.RxJavaUncaughtErrorRule
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

class SampleTest {

    @get:Rule
    val uncaughtRxJavaErrors = RxJavaUncaughtErrorRule()

    private lateinit var sample: Sample

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        RxJavaPlugins.setComputationSchedulerHandler {
            Schedulers.trampoline()
        }
        RxAndroidPlugins.setMainThreadSchedulerHandler {
            Schedulers.trampoline()
        }

        sample = Sample()
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()

        sample.close()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Sample
    ///////////////////////////////////////////////////////////////////////////

    @Test(expected = AssertionError::class)
    fun sample1_fail() {
        Single.just(1)
            .subscribe { value ->
                fail("Result:$value")
            }
    }

    private fun testCase(callback: (Int) -> Unit) {
        Single.just(1)
            .subscribe { value ->
                callback(value)
            }
    }

    @Test(expected = AssertionError::class)
    fun sample_mockito1() {
        val mockCallback: (Int) -> Unit = mock()
        testCase(mockCallback)

        val paramCapture = argumentCaptor<Int>()
        verify(mockCallback).invoke(paramCapture.capture())

        fail("Result:${paramCapture.firstValue}")
    }

    @Test(expected = AssertionError::class)
    fun sample_mockito2() {
        val mockCallback: (Int) -> Unit = mock()
        testCase(mockCallback)

        verify(mockCallback).invoke(eq(0))
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parameter 1 Test
    ///////////////////////////////////////////////////////////////////////////

    // value : 1
    // 해당 케이스는 실패해야지 정상
    @Test(expected = AssertionError::class)
    fun t1_fail() {
        sample.testParam1 { value ->
            assertTrue(value > 100)
        }
    }

    // value : 1
    // 해당 케이스는 실패해야지 정상
    @Test(expected = AssertionError::class)
    fun t1_success() {
        sample.testParam1 { value ->
            assertTrue(value > 100)
        }

        uncaughtRxJavaErrors.assertNoErrors()
    }

    // value : 1
    // 해당 케이스는 실패해야지 정상
    // testParam1로 넘겨진 콜백이 불려지기전에 실패한 경우는 불가능
    @Test(expected = AssertionError::class)
    fun t1_success2() {
        val mockCallback: (Int) -> Unit = mock()
        sample.testParam1(mockCallback)

        val paramCapture = argumentCaptor<Int>()
        verify(mockCallback).invoke(paramCapture.capture())
        assertTrue(paramCapture.firstValue > 100)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Parameter 2 Test
    ///////////////////////////////////////////////////////////////////////////

    // list : 1..10
    // key : 1
    // 해당 케이스는 실패해야지 정상
    @Test(expected = AssertionError::class)
    fun t2_fail() {
        sample.testParam2 { list, _ ->
            assertTrue(list.isEmpty())
        }
    }

    // list : 1..10
    // key : 1
    // 해당 케이스는 실패해야지 정상
    @Test(expected = AssertionError::class)
    fun t2_success() {
        sample.testParam2 { list, _ ->
            assertTrue(list.isEmpty())
        }

        uncaughtRxJavaErrors.assertNoErrors()
    }

    // list : 1..10
    // key : 1
    // 해당 케이스는 실패해야지 정상
    // testParam2로 넘겨진 콜백이 불려지기전에 실패한 경우는 불가능
    @Test(expected = AssertionError::class)
    fun t2_success2() {
        val mockCallback: (List<Int>, Int?) -> Unit = mock()
        sample.testParam2(mockCallback)

        val argumentCaptor1 = argumentCaptor<List<Int>>()
        val argumentCaptor2 = argumentCaptor<Int>()
        verify(mockCallback, times(1)).invoke(argumentCaptor1.capture(), argumentCaptor2.capture())

        assertEquals((1..10).toList(), argumentCaptor1.firstValue)
        assertNull(argumentCaptor2.firstValue)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listener Param 1 Test
    ///////////////////////////////////////////////////////////////////////////

    // 해당 케이스는 실패해야지 정상
    @Test(expected = AssertionError::class)
    fun t3_fail() {
        sample.testListenerParam1(object : Sample.SampleListener {
            override fun callback1(data: Int) {
                fail("실패 테스트1")
            }

            override fun callback2(data: List<Int>, key: Int?) {
                fail("실패 테스트2")
            }
        })
    }

    // 해당 케이스는 실패해야지 정상
    @Test(expected = AssertionError::class)
    fun t3_success() {
        sample.testListenerParam1(object : Sample.SampleListener {
            override fun callback1(data: Int) {
                fail("실패 테스트1")
            }

            override fun callback2(data: List<Int>, key: Int?) {
                fail("실패 테스트2")
            }
        })

        uncaughtRxJavaErrors.assertNoErrors()
    }

    // 해당 케이스는 실패해야지 정상
    // testListenerParam1로 넘겨진 콜백이 불려지기전에 실패한 경우는 불가능
    @Test(expected = AssertionError::class)
    fun t3_success2() {
        val mockCallback: Sample.SampleListener = mock()
        sample.testListenerParam1(mockCallback)

        verify(mockCallback, never()).callback1(any())
        verify(mockCallback, never()).callback2(any(), anyOrNull())
    }
}